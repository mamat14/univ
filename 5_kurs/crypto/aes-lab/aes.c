#include <stdio.h>
#include <stdlib.h>
#include "aes.h"

int prev_state[4][Nb];

void printState(byte state[4][Nb], byte round, char *prefix) {
    printf("round[%d].", round);
    printf("%s", prefix);
    printf(" ");
    for (int j = 0; j < Nb; j++) {
        for (int i = 0; i < 4; i++) {
            printf("%02x", state[i][j]);
        }
    }
    printf("\n");
}

void printKeySchedule(word w[Nb * (Nr + 1)], byte round) {
    int l, c;
    printf("round[%d].k_sch ", round);
    for (c = 0, l = round * Nb; c < Nb; ++c) {
        printf("%08x", w[l + c]);
    }
    printf("\n");
}

void SubBytes(byte state[4][Nb]) {
    int i, j;
    for (i = 0; i < 4; i++) {
        for (j = 0; j < 4; j++) {
            byte b = state[i][j];
            byte col = b >> 4;
            byte row = b & 0x0f;
            state[i][j] = sbox[col][row];
        }
    }
}

byte shift(byte a, byte b) {
    if (a == 1 && b == 4) {
        return 1;
    } else if (a == 2 && b == 4) {
        return 2;
    } else if (a == 3 && b == 4) {
        return 3;
    }
    exit(3);
}

//void ShiftRows(byte state[4][Nb]) {
//    int tmp, tmp2;
//    tmp = state[1][0];
//    memcpy(&state[1][0], &state[1][1], sizeof(byte) * (Nb - 1));
//    state[1][Nb - 1] = tmp;
//
//    tmp = state[2][0];
//    tmp2 = state[2][1];
//    memcpy(&state[2][0], &state[2][2], sizeof(byte) * (Nb - 2));
//    state[2][Nb - 2] = tmp;
//    state[2][Nb - 1] = tmp2;
//
//    tmp = state[3][Nb - 1];
//    memcpy(&state[3][1], &state[3][0], sizeof(byte) * (Nb - 1));
//    state[3][0] = tmp;
//}

//void ShiftRows(byte state[4][Nb]) {
//    int tmp,r,c,tmp2,tmp3, tmp4 = 0;
//    for(r = 1; r < 4; ++r) {
//        if(r == 1) {
//            tmp2 = state[r][0];
//            for(c = 0; c < Nb-1; c++) {
//                tmp = (c + shift(r, Nb)) % Nb;
//                state[r][c] = state[r][tmp];
//            }
//            state[r][Nb-1] = tmp2;
//        } else if(r == 2) {
//            tmp2 = state[r][0];
//            tmp3 = state[r][1];
//            for(c = 0; c < Nb-2; c++) {
//                tmp = (c + shift(r, Nb)) % Nb;
//                state[r][c] = state[r][tmp];
//            }
//            state[r][Nb-2] = tmp2;
//            state[r][Nb-1] = tmp3;
//        } else if(r == 3) {
//            tmp2 = state[r][0];
//            tmp3 = state[r][1];
//            tmp4 = state[r][2];
//            for(c = 0; c < Nb - 3; c++) {
//                tmp = (c + shift(r, Nb)) % Nb;
//                state[r][c] = state[r][tmp];
//            }
//            state[r][Nb-3] = tmp2;
//            state[r][Nb-2] = tmp3;
//            state[r][Nb-1] = tmp4;
//        } else {
//            exit(4);
//        }
//    }
//}

void ShiftRows(byte state[4][Nb]) {
    int tmp, r, c = 0;
    for (r = 0; r < 4; ++r) {
        for (c = 0; c < Nb; ++c) {
            prev_state[r][c] = state[r][c];
        }
    }

    for (r = 1; r < 4; ++r) {
        for (c = 0; c < Nb; c++) {
            tmp = (c + shift(r, Nb)) % Nb;
            state[r][c] = prev_state[r][tmp];
        }
    }
}

byte xtime(byte op) {
    unsigned short int tmp = op << 1;
    if (tmp <= 0xff) {
        return (byte) tmp;
    }
    return tmp ^ 0x11b;
}

byte mult(byte a, byte b) {
    byte prev;
    byte ans = 0;
    byte curr = a;
    unsigned short int mask;
    for (mask = 1; mask <= b; mask <<= 1) {
        prev = curr;
        curr = xtime(prev);
        if (b & mask) {
            ans ^= prev;
        }
    }
    return ans;
}

void MixColumns(byte state[4][4]) {
    int col;
    for (col = 0; col < 4; col++) {
        byte b1 = mult(0x02, state[0][col]) ^mult(0x03, state[1][col]) ^state[2][col] ^state[3][col];
        byte b2 = state[0][col] ^mult(0x02, state[1][col]) ^mult(0x03, state[2][col]) ^state[3][col];
        byte b3 = state[0][col] ^state[1][col] ^mult(0x02, state[2][col]) ^mult(0x03, state[3][col]);
        byte b4 = mult(0x03, state[0][col]) ^state[1][col] ^state[2][col] ^mult(0x02, state[3][col]);

        state[0][col] = b1;
        state[1][col] = b2;
        state[2][col] = b3;
        state[3][col] = b4;
    }
}

void AddRoundKey(byte state[4][Nb], word w[Nb * (Nr + 1)], byte round) {
    int l, c;
    for (c = 0, l = round * Nb; c < Nb; ++c) {
        state[0][c] ^= (w[l + c] >> 24 & 0xff);
        state[1][c] ^= (w[l + c] >> 16 & 0xff);
        state[2][c] ^= (w[l + c] >> 8 & 0xff);
        state[3][c] ^= (w[l + c] & 0xff);
    }
}

word SubWord(word w) {
    word result = 0;
    int i;
    for (i = 0; i < 4; i++) {
        byte cur_byte = (w >> (8 * i) & 0xff);
        byte col = cur_byte >> 4;
        byte row = cur_byte & 0x0F;
        byte newByte = sbox[col][row];
        result |= (newByte << (8 * i));
    }
    return result;
}

word RotWord(word w) {
    word result = 0;
    int i;
    for (i = 3; i >= 0; i--) {
        byte cur = (w >> (8 * i) & 0xff);
        byte destIdx = i == 3 ? 0 : i + 1;
        result |= (cur << (8 * destIdx));
    }
    return result;
}

void KeyExpansion(byte key[4 * Nk], word w[Nb * (Nr + 1)]) {
    word temp;
    int i = 0;
    while (i < Nk) {
        w[i] = ((word) key[4 * i] << 24 | (word) key[4 * i + 1] << 16 | (word) key[4 * i + 2] << (word) 8 |
                key[4 * i + 3]);
        ++i;
    }
    while (i < Nb * (Nr + 1)) {
        temp = w[i - 1];
        if (i % Nk == 0) {
            temp = SubWord(RotWord(temp)) ^ rcon[i / Nk];
        } else if (Nk > 6 && i % Nk == 4) {
            temp = SubWord(temp);
        }
        w[i] = w[i - Nk] ^ temp;
        ++i;
    }
}

void cypher(byte *inp, byte *out, word *w) {
    byte state[4][Nb];
    int i = 0, row, col;
    for (col = 0; col < Nb; col++) {
        for (row = 0; row < 4; row++) {
            state[row][col] = inp[i++];
        }
    }

    AddRoundKey(state, w, 0);
    int round;
    for (round = 1; round < Nr; ++round) {
        if (debug) printState(state, round, "start");
        SubBytes(state);
        if (debug) printState(state, round, "s_box");
        ShiftRows(state);
        if (debug) printState(state, round, "s_row");
        MixColumns(state);
        if (debug) printState(state, round, "m_col");
        AddRoundKey(state, w, round);
        if (debug) printKeySchedule(w, round);
    }

    if (debug) printState(state, round, "start");
    SubBytes(state);
    if (debug) printState(state, round, "s_box");
    ShiftRows(state);
    if (debug) printState(state, round, "s_row");
    AddRoundKey(state, w, Nr);
    if (debug) printKeySchedule(w, round);

    i = 0;
    for (col = 0; col < Nb; col++) {
        for (row = 0; row < 4; row++) {
            out[i++] = state[row][col];
        }
    }
}

void swap(byte arr[4][4], byte i_1, byte j_1, byte i_2, byte j_2) {
    byte tmp = arr[i_1][j_1];
    arr[i_1][j_1] = arr[i_2][j_2];
    arr[i_2][j_2] = tmp;
}

void InvShiftRows(byte state[4][4]) {
    int tmp, r, c = 0;
    for (r = 0; r < 4; ++r) {
        for (c = 0; c < Nb; ++c) {
            prev_state[r][c] = state[r][c];
        }
    }

    for (r = 1; r < 4; ++r) {
        for (c = 0; c < Nb; c++) {
            tmp = (c + shift(r, Nb)) % Nb;
            state[r][tmp] = prev_state[r][c];
        }
    }
}

void InvMixColumns(byte state[4][4]) {
    int col;
    for (col = 0; col < 4; col++) {
        byte b1 = mult(0x0e, state[0][col]) ^mult(0x0b, state[1][col]) ^mult(0x0d, state[2][col]) ^mult(0x09, state[3][col]);
        byte b2 = mult(0x09, state[0][col]) ^mult(0x0e, state[1][col]) ^mult(0x0b, state[2][col]) ^mult(0x0d, state[3][col]);
        byte b3 = mult(0x0d, state[0][col]) ^mult(0x09, state[1][col]) ^mult(0x0e, state[2][col]) ^mult(0x0b, state[3][col]);
        byte b4 = mult(0x0b, state[0][col]) ^mult(0x0d, state[1][col]) ^mult(0x09, state[2][col]) ^mult(0x0e, state[3][col]);

        state[0][col] = b1;
        state[1][col] = b2;
        state[2][col] = b3;
        state[3][col] = b4;
    }
}

void InvSubBytes(byte state[4][Nb]) {
    int i, j;
    for (i = 0; i < 4; i++) {
        for (j = 0; j < 4; j++) {
            byte b = state[i][j];
            byte col = b >> 4;
            byte row = b & 0x0F;
            state[i][j] = invSbox[col][row];
        }
    }
}

void invCypher(byte *inp, byte *out, word *w) {
    byte state[4][Nb];
    int i = 0, row, col;
    for (col = 0; col < Nb; col++) {
        for (row = 0; row < 4; row++) {
            state[row][col] = inp[i++];
        }
    }

    int round;
    AddRoundKey(state, w, Nr);
    for (round = Nr - 1; round > 0; --round) {
        if (debug) printState(state, round, "start");
        InvShiftRows(state);
        if (debug) printState(state, round, "s_row");
        InvSubBytes(state);
        if (debug) printState(state, round, "s_box");
        AddRoundKey(state, w, round);
        if (debug) printKeySchedule(w, round);
        InvMixColumns(state);
        if (debug) printState(state, round, "m_col");
    }

    if (debug) printState(state, round, "start");
    InvShiftRows(state);
    if (debug) printState(state, round, "s_row");
    InvSubBytes(state);
    if (debug) printState(state, round, "s_box");
    AddRoundKey(state, w, round);
    if (debug) printKeySchedule(w, round);

    i = 0;
    for (col = 0; col < Nb; col++) {
        for (row = 0; row < 4; row++) {
            out[i++] = state[row][col];
        }
    }
}


