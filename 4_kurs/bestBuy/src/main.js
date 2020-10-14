"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//probability that given maximum point x(i) = a_k we will
//get next maximum point x(i+1) = a_l
function p(k, l) {
    if (k >= l) {
        return 0;
    }
    else {
        return k / (l * (l - 1));
    }
}
exports.p = p;
//# sourceMappingURL=main.js.map