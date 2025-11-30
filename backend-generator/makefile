CC = gcc
LEX = flex
YACC = bison

CFLAGS = -Wall -Wextra -O2

OBJS = config.tab.o lex.yy.o main.o file_util.o \
       pom_generator.o resources_generator.o java_core_generator.o \
       security_generator.o user_generator.o auth_generator.o demo_generator.o \
       init_generator.o crud_generator.o

all: backendgen

config.tab.c config.tab.h: config.y
	$(YACC) -d config.y

lex.yy.c: config.l config.tab.h
	$(LEX) config.l

backendgen: $(OBJS)
	$(CC) $(CFLAGS) -o backendgen $(OBJS) -lfl

%.o: %.c
	$(CC) $(CFLAGS) -c $<

clean:
	rm -f backendgen *.o config.tab.c config.tab.h lex.yy.c