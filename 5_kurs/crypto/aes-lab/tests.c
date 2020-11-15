#include <assert.h>
#include <stdio.h>
#include "aes.h"

void testKeyExpansion128Bits() {
    printf("Testing key expansion...\n");
    byte key[16] = { 0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6, 0xab, 0xf7, 0x15, 0x88, 0x09, 0xcf, 0x4f, 0x3c};
    word expansion[44] = {0};
    KeyExpansion(key, expansion);
    assert(expansion[4] == 0xa0fafe17);
    assert(expansion[8] == 0xf2c295f2);
    assert(expansion[21] == 0x7c839d87);
    assert(expansion[42] == 0xe13f0cc8);
    assert(expansion[43] == 0xb6630ca6);
    printf("Key expansion OK!\n");
}

void testRoundKey() {
    printf("testing AddRoundKey...\n");
    byte state[][4] = {{0x00, 0x44, 0x88, 0xcc},
                           {0x11, 0x55, 0x99, 0xdd},
                           {0x22, 0x66, 0xaa, 0xee},
                           {0x33, 0x77, 0xbb, 0xff}};
    byte expectedOutputState[][4]= {{0x00, 0x40, 0x80, 0xc0},
                                    {0x10, 0x50, 0x90, 0xd0},
                                    {0x20, 0x60, 0xa0, 0xe0},
                                    {0x30, 0x70, 0xb0, 0xf0}};

    word schedule[44] = {0};
    schedule[0] = 0x00010203;
    schedule[1] = 0x04050607;
    schedule[2] = 0x08090a0b;
    schedule[3] = 0x0c0d0e0f;
    AddRoundKey(state, schedule, 0);
    for(int i = 0; i < 4; i++) {
        for(int j = 0; j < 4; j++) {
            assert(state[i][j] == expectedOutputState[i][j]);
        }
    }
    printf("AddRoundKey OK\n");
}

void testCypher() {
    printf("Testing cypher...\n");
    byte plaintext[16] = {0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff};
    byte out[16] = {0};
    byte expected[16]= {0x69, 0xc4, 0xe0, 0xd8, 0x6a, 0x7b, 0x04, 0x30, 0xd8, 0xcd, 0xb7, 0x80, 0x70, 0xb4, 0xc5, 0x5a};
    byte key[16] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    word schedule[44] = {0};
    KeyExpansion(key, schedule);
    cypher(plaintext, out, schedule);
    for(int i = 0; i < 16; ++i) {
        assert(out[i] == expected[i]);
    }
    printf("Testing cypher OK.");
}

void testInvCypher() {
    printf("Testing invCypher...\n");
    byte plaintext[16] = {0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99, 0xaa, 0xbb, 0xcc, 0xdd, 0xee, 0xff};
    byte cypheredText[16] = {0};
    byte decypheredText[16] = {0};
    byte key[16] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    word schedule[44];
    KeyExpansion(key, schedule);

    cypher(plaintext, cypheredText, schedule);

    invCypher(cypheredText, decypheredText, schedule);
    for(int i = 0; i < 16; ++i) {
        assert(plaintext[i] == decypheredText[i]);
    }
    printf("Testing invCypher OK.");
}

int main(int argc, char *argv[]) {
    testKeyExpansion128Bits();
    testRoundKey();
    testCypher();
    testInvCypher();
    return 0;
}
