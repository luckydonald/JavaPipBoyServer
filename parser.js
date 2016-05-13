function Renderer (bytes) {
    this.bytes = bytes;
    this.pos = 0;
}
Renderer.prototype.decode0 = function (array) {
    return new TextDecoder("UTF-8").decode(array);
};
Renderer.prototype.decode1 = function (array) {
    var encodedString = String.fromCharCode.apply(null, array   ),
        decodedString = decodeURIComponent(escape(encodedString));
    return decodedString;
};
Renderer.prototype.decode2 = function (array) {
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
Renderer.prototype.decode  = function (array) {
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

Renderer.prototype.types = ["boolean", "int8", "uint8", "int32", "uint32", "float", "string", "array", "dict"];

Renderer.prototype.next_int = function(values_to_read) {
    var int = 0;
    for (var i = 0; i < values_to_read; i++) {
        int += (this.bytes[this.pos + i] << (8 * i));
    }
    return int;
};

Renderer.prototype.new_byte = function(byte) {
    byte = (byte !== undefined ? byte : this.bytes[this.pos++]);
    var obj = $("<div>");
    obj.addClass("byte");
    obj.text(byte);
    return obj;
};
Renderer.prototype.new_obj_bytes = function () {
    var obj = $("<div>");
    obj.addClass("bytes");
    return obj;
};
Renderer.prototype.new_type = function(type, values_to_read, calculated) {
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
    label.text(type);
    obj.append(label);
    var value = $("<div>");
    value.addClass("calculated");
    value.text(calculated === undefined ? "-" : calculated);
    obj.append(value);
    return obj;
};
Renderer.prototype.new_boolean = function() {
    var obj = $("<div>");
    obj.addClass("bool");
    var bool = this.next_int(1);
    obj.append(this.new_type("value", 1, (bool == 0 ? "false" : "true")));
    //elem.append(obj);
    return obj;
};
Renderer.prototype.new_intX = function(values_to_read) {
    var obj = $("<div>");
    obj.addClass("int" + (8*values_to_read));
    var int = this.next_int(values_to_read);
    obj.append(this.new_type("value", values_to_read, int));
    return obj;
};

Renderer.prototype.new_int8 = function() {
    return this.new_intX(1);
};
Renderer.prototype.new_int32 = function() {
    return this.new_intX(4);
};
/**
 *
 * @param is_key false/undefined (default): Add a normal String element. true: omit the outer object, only add the part.
 * @returns {*|jQuery|HTMLElement}
 */
Renderer.prototype.new_string = function(is_key) {
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
Renderer.prototype.new_list = function() {
    var obj = $("<div>");
    obj.addClass("list");
    var count = this.next_int(2);
    obj.append(this.new_type("count", 2, count));
    for (var i = 0; i < count; i++) {
        var elem = this.new_id();
        obj.append(elem);
    }
    return obj;
};
Renderer.prototype.new_dict = function() {
    var obj = $("<div>");
    obj.addClass("dict");
    var count = this.next_int(2);
    obj.append(this.new_type("count", 2, count));
    for (var i = 0; i < count; i++) {
        var elem = this.new_id();
        obj.append(elem);
        var key = this.new_string(true);
        obj.append(key);
    }
    obj.append(this.new_type("count", 2, count));
    for (var i = 0; i < count; i++) {
        var elem = this.new_id();
        obj.append(elem);
    }
    return obj;
};
Renderer.prototype.new_id = function () {
    var id_int = this.next_int(4);
    var id_obj = this.new_type("id", 4, id_int);
    id_obj.data("value", id_int);
    //id_obj.onclick("Renderer.show_element($(this).data(\"value\"))");
    return id_obj
};
Renderer.prototype.new_whatever = function() {
    var type = this.bytes[this.pos];
    var type_obj = this.new_type("type", 1, this.types[type]);
    var id_obj = this.new_id();
    switch (type) {
        case 0: //BOOLEAN
            obj = this.new_boolean();
            break;
        case 1:case 2: //INT8
            obj = this.new_int8();
            break;
        case 3:case 4: //INT32
            obj = this.new_int32();
            break;
        case 6: //STRING
            obj = this.new_string();
            break;
        case 7: //LIST
            obj = this.new_list();
            break;
        case 8: //DICT
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


function func_render() {
    var input = document.getElementById("input");
    var text = input.value;
    var the_list = JSON.parse(text);
    var renderer = new Renderer(the_list);
    var output = $("#output");
    output.empty();
    while (renderer.pos < renderer.bytes.length) {
        output.append(renderer.new_whatever());
    }
}
function func_use_single_row () {
    var output = $("#output");
    console.log($("#use_single_row").is(":checked"), output);
    if($("#use_single_row").is(":checked")) {
        output.addClass("single_row");
    } else {
        output.removeClass("single_row");
    }
}