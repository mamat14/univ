CC=gcc
CFLAGS=-I -O3
DEPS = aes.h
OBJ = dist/aes.o dist/tests.o
STRESS_TEST_OBJ = dist/aes.o dist/stress_test.o

dist/%.o: %.c $(DEPS)
	$(CC) -c -o $@ $< $(CFLAGS)

tests: $(OBJ)
	$(CC) -o dist/$@ $^ $(CFLAGS)

stressTest: $(STRESS_TEST_OBJ)
	$(CC) -o dist/$@ $^ $(CFLAGS)

test: tests
	./dist/tests

run: stressTest
	./dist/stressTest

clean:
	rm -rf dist; mkdir "dist"
