
kupyna-test: kupyna.c kupyna.h kupyna-test.c tables.c tables.h
	gcc kupyna.c kupyna-test.c tables.c -o kupyna-test

kupyna-test-run: kupyna-test
	./kupyna-test

sha256-test: sha256.c sha256.h sha256-test.c
	gcc sha256.c sha256-test.c -o sha256-test

sha256-test-run: sha256-test
	./sha256-test

test-speed-exe: sha256.c sha256.h kupyna.c kupyna.h tables.c tables.h hash-a-file.c
	gcc sha256.c kupyna.c tables.c hash-a-file.c -o test-speed-exe

test-speed: test-speed-exe
	./test-speed-exe
