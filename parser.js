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
Renderer.prototype.new_type = function(type, values_to_read, calculated) {
    var obj = $("<div>");
    obj.addClass(type).addClass("part");
    for(var i = 0; i < values_to_read; i++)
    {
        obj.append(this.new_byte());
    }
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
Renderer.prototype.new_string = function() {
    var obj_string = $("<div>");
    obj_string.addClass("string");
    var obj = $("<div>");
    obj.addClass("value").addClass("part");
    var int_parts = [];
    while (this.bytes[this.pos] != 0) {
        int_parts[int_parts.length] = this.bytes[this.pos];
        obj.append(this.new_byte());
    }
    var uin8array = new Uint8Array(int_parts.length);
    for (var i = 0; i < int_parts.length; i++) {
        uin8array[i] = int_parts[i];
    }
    var str = this.decode(uin8array);
    obj.append(this.new_byte());  // the skippend "\0"
    var label = $("<div>");
    label.addClass("caption");
    label.text("string");
    obj.append(label);
    var value = $("<div>");
    value.addClass("calculated");
    value.text(str);
    obj.append(value);
    obj_string.append(obj);
    return obj_string;
};
Renderer.prototype.new_list = function() {
    var obj = $("<div>");
    obj.addClass("list");
    obj.append(this.new_type("count", 4));
    return obj;
};
Renderer.prototype.new_dict = function() {
    var obj = $("<div>");
    obj.addClass("dict");
    obj.append(this.new_type("count", 4));
    return obj;
};
Renderer.prototype.new_whatever = function() {
    var type = this.bytes[this.pos];
    var type_obj = this.new_type("type", 1, this.types[type]);
    var id_int = this.next_int(4);
    var id_obj = this.new_type("id", 4, id_int);
    switch (type) {
        case 0: //BOOLEAN
            obj = this.new_boolean();
            break;
        case 1:
            obj = this.new_int8();
            break;
        case 3:
            obj = this.new_int32();
            break;
        case 6:
            obj = this.new_string();
            break;
        case 7:
            obj = this.new_list();
            break;
        case 8:
            obj = this.new_dict();
            break;
        default:
            var obj = $("<div>");
            obj.text("nope");
            break;
    }
    obj.prepend(id_obj);
    obj.prepend(type_obj);
    return obj;

};
function render() {
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