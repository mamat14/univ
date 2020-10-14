//probability that given maximum point x(i) = a_k we will
//get next maximum point x(i+1) = a_l
export function p(k: number, l: number) {
    if (k >= l) {
        return 0
    } else {
        return k / (l * (l-1));
    }
}