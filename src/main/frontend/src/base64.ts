
const lookup = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';

const PLUS   = '+'.charCodeAt(0);
const SLASH  = '/'.charCodeAt(0);
const NUMBER = '0'.charCodeAt(0);
const LOWER  = 'a'.charCodeAt(0);
const UPPER  = 'A'.charCodeAt(0);

function decode (elt: string): number {
    let code = elt.charCodeAt(0);
    if (code === PLUS)
        return 62; // '+'
    if (code === SLASH)
        return 63; // '/'
    if (code < NUMBER)
        return -1; //no match
    if (code < NUMBER + 10)
        return code - NUMBER + 26 + 26;
    if (code < UPPER + 26)
        return code - UPPER;
    if (code < LOWER + 26)
        return code - LOWER + 26
}

function toByteArray(b64: string): Uint8Array {
    let i, j, l, tmp, placeHolders, arr;

    if (b64.length % 4 > 0) {
        throw new Error('Invalid string. Length must be a multiple of 4')
    }

    // the number of equal signs (place holders)
    // if there are two placeholders, than the two characters before it
    // represent one byte
    // if there is only one, then the three characters before it represent 2 bytes
    // this is just a cheap hack to not do indexOf twice
    let len = b64.length;
    placeHolders = '=' === b64.charAt(len - 2) ? 2 : '=' === b64.charAt(len - 1) ? 1 : 0;

    // base64 is 4/3 + up to two characters of the original data
    arr = new Uint8Array(b64.length * 3 / 4 - placeHolders);

    // if there are placeholders, only get up to the last complete 4 chars
    l = placeHolders > 0 ? b64.length - 4 : b64.length;

    let L = 0;

    function push (v: number) {
        arr[L++] = v
    }

    for (i = 0, j = 0; i < l; i += 4, j += 3) {
        tmp = (decode(b64.charAt(i)) << 18)
            | (decode(b64.charAt(i + 1)) << 12)
            | (decode(b64.charAt(i + 2)) << 6)
            | decode(b64.charAt(i + 3));
        push((tmp & 0xFF0000) >> 16);
        push((tmp & 0xFF00) >> 8);
        push(tmp & 0xFF)
    }

    if (placeHolders === 2) {
        tmp = (this.decode(b64.charAt(i)) << 2) | (this.decode(b64.charAt(i + 1)) >> 4);
        push(tmp & 0xFF)
    } else if (placeHolders === 1) {
        tmp = (this.decode(b64.charAt(i)) << 10) | (this.decode(b64.charAt(i + 1)) << 4) | (this.decode(b64.charAt(i + 2)) >> 2);
        push((tmp >> 8) & 0xFF);
        push(tmp & 0xFF)
    }

    return arr
}


function fromByteArray(uint8: Uint8Array): string {
    let i,
        extraBytes = uint8.length % 3, // if we have 1 byte left, pad 2 bytes
        output = "",
        temp, length;

    function encode (num: number) {
        return lookup.charAt(num)
    }

    function tripletToBase64 (num) {
        return encode(num >> 18 & 0x3F) + encode(num >> 12 & 0x3F) + encode(num >> 6 & 0x3F) + encode(num & 0x3F)
    }

    // go through the array every three bytes, we'll deal with trailing stuff later
    for (i = 0, length = uint8.length - extraBytes; i < length; i += 3) {
        temp = (uint8[i] << 16) + (uint8[i + 1] << 8) + (uint8[i + 2]);
        output += tripletToBase64(temp)
    }

    // pad the end with zeros, but make sure to not forget the extra bytes
    switch (extraBytes) {
        case 1:
            temp = uint8[uint8.length - 1];
            output += encode(temp >> 2);
            output += encode((temp << 4) & 0x3F);
            output += '==';
            break;
        case 2:
            temp = (uint8[uint8.length - 2] << 8) + (uint8[uint8.length - 1]);
            output += encode(temp >> 10);
            output += encode((temp >> 4) & 0x3F);
            output += encode((temp << 2) & 0x3F);
            output += '=';
            break
    }

    return output
}

export {fromByteArray, toByteArray}




