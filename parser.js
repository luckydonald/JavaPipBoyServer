var Application = {};
function ByteStringParser (bytes) {
    this.bytes = bytes;
    var buffer = new ArrayBuffer(bytes.length);
    for (var i = 0; i < bytes.length; i++) {
        buffer[i] = bytes[i];
    }
    this.data = new DataView(buffer);
    this.pos = 0;
}
function Renderer (name, desc, func) {
    this.name = name;
    this.desc = desc;
    this.func = func;
}
function Parser (name, desc, func) {
    this.name = name;
    this.desc = desc;
    this.func = func;
}
var parsers = [];
var renders = [];


ByteStringParser.prototype.decode0 = function (array) {
    return new TextDecoder("UTF-8").decode(array);
};
ByteStringParser.prototype.decode1 = function (array) {
    var encodedString = String.fromCharCode.apply(null, array   ),
        decodedString = decodeURIComponent(escape(encodedString));
    return decodedString;
};
ByteStringParser.prototype.decode2 = function (array) {
    var out, i, len, c;
    var char2, char3;

    out = "";
    len = array.length;
    i = 0;
    while(i < len) {
        c = array[i++];
        switch(c >> 4)
        {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
            // 0xxxxxxx
            out += String.fromCharCode(c);
            break;
            case 12: case 13:
            // 110x xxxx   10xx xxxx
            char2 = array[i++];
            out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
            break;
            case 14:
                // 1110 xxxx  10xx xxxx  10xx xxxx
                char2 = array[i++];
                char3 = array[i++];
                out += String.fromCharCode(((c & 0x0F) << 12) |
                    ((char2 & 0x3F) << 6) |
                    ((char3 & 0x3F) << 0));
                break;
        }
    }

    return out;
};
ByteStringParser.prototype.decode  = function (array) {
    try {
        return this.decode0(array);
    } catch (err) {
        console.warn(err);
        try {
            return this.decode1(array);
        } catch (err){
            console.warn(err);
            return this.decode2(array);
        }
    }
};

ByteStringParser.prototype.types = ["boolean", "int8", "uint8", "int32", "uint32", "float", "string", "array", "dict"];

ByteStringParser.prototype.next_int = function(values_to_read) {
    var int = 0;
    for (var i = 0; i < values_to_read; i++) {
        int += (this.bytes[this.pos + i] << (8 * i));
    }
    return int;
};

ByteStringParser.prototype.new_byte = function(byte) {
    byte = (byte !== undefined ? byte : this.bytes[this.pos++]);
    var obj = $("<div>");
    obj.addClass("byte");
    obj.data("byte", byte);
    obj.text(Application.render(byte));
    return obj;
};
ByteStringParser.prototype.new_obj_bytes = function () {
    var obj = $("<div>");
    obj.addClass("bytes");
    return obj;
};
ByteStringParser.prototype.new_something = function(type, values_to_read, calculated, name) {
    if (name === undefined || name === null) {
        name = type;
    }
    var obj = $("<div>");
    obj.addClass(type).addClass("part");
    var bytes_obj = this.new_obj_bytes();
    for(var i = 0; i < values_to_read; i++)
    {
        bytes_obj.append(this.new_byte());
    }
    obj.append(bytes_obj);
    var label = $("<div>");
    label.addClass("caption");
    label.text(name);
    obj.append(label);
    var value = $("<div>");
    value.addClass("calculated");
    value.text(calculated === undefined ? "-" : calculated);
    obj.append(value);
    return obj;
};
ByteStringParser.prototype.new_boolean = function() {
    var obj = $("<div>");
    obj.addClass("bool");
    var bool = this.next_int(1);
    obj.append(this.new_something("value", 1, (bool == 0 ? "false" : "true")));
    //elem.append(obj);
    return obj;
};
ByteStringParser.prototype.new_intX = function(values_to_read) {
    var obj = $("<div>");
    obj.addClass("int" + (8*values_to_read));
    var int = this.next_int(values_to_read);
    obj.append(this.new_something("value", values_to_read, int));
    return obj;
};
ByteStringParser.prototype.new_int8 = function() {
    return this.new_intX(1);
};
ByteStringParser.prototype.new_int32 = function() {
    return this.new_intX(4);
};
ByteStringParser.prototype.new_float = function() {
    var obj = $("<div>");
    obj.addClass("float");
    //var float = Application.parseFloat();
    //var int = this.next_int(values_to_read);
    obj.append(this.new_something("value", values_to_read, "little Endian"));
    return obj;
};
/**
 *
 * @param is_key false/undefined (default): Add a normal String element. true: omit the outer object, only add the part.
 * @returns {*|jQuery|HTMLElement}
 */
ByteStringParser.prototype.new_string = function(is_key) {
    var obj_string = $("<div>");
    obj_string.addClass("string");
    var obj = $("<div>");
    obj.addClass("part");
    obj.addClass(is_key === true ? "key" : "value");
    var int_parts = [];
    var bytes_obj = this.new_obj_bytes();
    while (this.bytes[this.pos] != 0) {
        int_parts[int_parts.length] = this.bytes[this.pos];
        bytes_obj.append(this.new_byte());
    }
    var uin8array = new Uint8Array(int_parts.length);
    for (var i = 0; i < int_parts.length; i++) {
        uin8array[i] = int_parts[i];
    }
    var str = this.decode(uin8array);
    bytes_obj.append(this.new_byte());  // the skippend "\0"
    obj.append(bytes_obj);
    var label = $("<div>");
    label.addClass("caption");
    label.text(is_key === true ? "key" : "string");
    obj.append(label);
    var value = $("<div>");
    value.addClass("calculated");
    value.text(str);
    obj.append(value);
    if (is_key === true) {
        return obj;
    }
    obj_string.append(obj);
    return obj_string;
};
ByteStringParser.prototype.new_list = function() {
    var obj = $("<div>");
    obj.addClass("list");
    var count = this.next_int(2);
    obj.append(this.new_something("count", 2, count, "count"));
    for (var i = 0; i < count; i++) {
        var elem = this.new_id();
        obj.append(elem);
    }
    return obj;
};
ByteStringParser.prototype.new_dict = function() {
    var obj = $("<div>");
    obj.addClass("dict");
    var count = this.next_int(2);
    obj.append(this.new_something("count", 2, count, "add count"));
    var i, elem;
    for (i = 0; i < count; i++) {
        elem = this.new_id();
        obj.append(elem);
        var key = this.new_string(true);
        obj.append(key);
    }
    count = this.next_int(2);
    obj.append(this.new_something("count", 2, count, "del. count"));
    for (i = 0; i < count; i++) {
        elem = this.new_id();
        obj.append(elem);
    }
    return obj;
};
ByteStringParser.prototype.new_id = function () {
    var id_int = this.next_int(4);
    var id_obj = this.new_something("id", 4, id_int);
    id_obj.data("value", id_int);
    //id_obj.onclick("ByteStringParser.show_element($(this).data(\"value\"))");
    return id_obj
};
ByteStringParser.prototype.new_whatever = function() {
    var type = this.bytes[this.pos];
    var type_obj = this.new_something("type", 1, this.types[type]);
    var id_obj = this.new_id();
    switch (type) {
        case 0: // BOOLEAN
            obj = this.new_boolean();
            break;
        case 1:case 2: // INT8
            obj = this.new_int8();
            break;
        case 3:case 4: // INT32
            obj = this.new_int32();
            break;
        case 6: // STRING
            obj = this.new_string();
            break;
        case 7: // LIST
            obj = this.new_list();
            break;
        case 8: // DICT
            obj = this.new_dict();
            break;
        default:
            var obj = $("<div>");
            obj.text("nope");
            break;
    }
    obj.addClass("element");
    obj.prepend(id_obj);
    obj.prepend(type_obj);
    return obj;

};
Application.load_parser = function () {
    var select = $("#parsers");
    for(var i = 0; i < parsers.length; i++) {
        var parser = parsers[i];
        if (!parser instanceof Parser) {
            continue;
        }
        var option = $("<option/>")
            .val(i)
            .text(parser.name)
            .attr("title", parser.desc)
            .data("func", parser.func);
        if (i == 0) {
            option.attr('selected',true);
        }
        select.append(option);
    }
};
Application.load_render = function () {
    var select = $("#renderers");
    for(var i = 0; i < renders.length; i++) {
        var parser = renders[i];
        if (!parser instanceof Renderer) {
            continue;
        }
        var option = $("<option/>")
            .val(i)
            .text(parser.name)
            .attr("title", parser.desc)
            .data("func", parser.func);
        if (i == 0) {
            option.attr('selected',true);
        }
        select.append(option);
    }
};
Application.get_parser = function () {
    return parsers[$("#parsers").val() || 0];
};
Application.get_render = function () {
    return renders[$("#renderers").val() || 0] || function (byte) {return ""+byte};
};
Application.parse = function (string) {
    return this.get_parser().func(string);
};
Application.render = function (byte) {
    return this.get_render().func(byte);
};
Application.parseFloat = function (str, radix) {
    var parts = str.split(".");
    if ( parts.length > 1 )
    {
        return parseInt(parts[0], radix) + parseInt(parts[1], radix) / Math.pow(radix, parts[1].length);
    }
    return parseInt(parts[0], radix);
};

parsers[parsers.length] = new Parser(
    "json list", "uses the build in json parser. Is capable of hex in a format of 0x2E.", JSON.parse
);
parsers[parsers.length] = new Parser(
    "hex blob", "all non-hex character are simply ignored.", function (string) {
        string = string.replace(/[^0-9a-f]+/gi, '');
        if(string.length % 2 === 1) {throw "not an even number of digits..."}
        var array = new Uint8Array(string.length/2);
        for (var i = 0; i < string.length-1; i += 2) {
            array[i/2] = parseInt(string.substring(i, i+2), 16);
        }
        return array;
    }
);
renders[renders.length] = new Renderer(
    "decimal", "negative possible.", function (byte) {return byte}
);
var to_positive_hex= function (byte) {
    var string = "0" + ((byte >>> 0).toString(16));
    return string.substring(string.length-2);
};
renders[renders.length] = new Renderer(
    "positive", "always positive", function (byte) {
        var hex=to_positive_hex(byte);
        return parseInt(hex, 16);
    }
);
renders[renders.length] = new Renderer(
    "hex", "without the 0x.. part.", to_positive_hex
);

function onload() {
    Application.load_parser();
    Application.load_render();
}

function func_render() {
    var input = document.getElementById("input").value;
    var the_list = Application.parse(input);
    var renderer = new ByteStringParser(the_list);
    var output = $("#output");
    output.empty();
    while (renderer.pos < renderer.bytes.length) {
        output.append(renderer.new_whatever());
    }
}
function func_use_single_row () {
    var output = $("#output");
    if($("#use_single_row").is(":checked")) {
        output.addClass("single_row");
    } else {
        output.removeClass("single_row");
    }
}