#include<stdio.h>
#include<stdlib.h>
#include "aes.h"
#include <time.h>

byte *gen_rdm_bytestream (size_t num_bytes)
{
    byte *stream = malloc (num_bytes);
    size_t i;

    for (i = 0; i < num_bytes; i++)
    {
        stream[i] = rand ();
    }

    return stream;
}

int main() {
    srand ((unsigned int) time (NULL));

    int n = 1000000;
    int step = 4*Nb;
    int i;
    byte *inp = gen_rdm_bytestream(n);
    byte *out = malloc(n);
    byte key[16] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    word schedule[44];
    KeyExpansion(key, schedule);

    clock_t start = clock();
    for (i = 0; i < n; i += step) {
        cypher(inp+i, out+i, schedule);
    }
    clock_t end = clock();
    float seconds = (float)(end - start) / CLOCKS_PER_SEC;
    printf("%d bytes took %f.\n", n, seconds);
    return 0;
}
