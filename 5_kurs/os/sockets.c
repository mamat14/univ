#define NELEMS(x)  (sizeof(x) / sizeof((x)[0]))


#define UNIX 0
#define INET 1
#define BLOCKING 0
#define NONBLOCKING 0
#define PORT 8000

#include<stdio.h>
#include<sys/socket.h>
#include<arpa/inet.h>	//inet_addr
#include "csapp.h"
#include <string.h>

void serverCode(int family, int socketType, int packet_size) {
	int socket_desc;
	struct sockaddr_in server;
	
	//Create socket
	socket_desc = Socket(AF_INET , SOCK_STREAM , 0);
	
	//Prepare the sockaddr_in structure
	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port = htons( PORT );
	
	//Bind
	Bind(socket_desc,(struct sockaddr *)&server , sizeof(server))
	puts("bind done");

	//Listen
	Listen(socket_desc , 3);
	//Accept and incoming connection
	puts("Waiting for incoming connections...");
	c = sizeof(struct sockaddr_in);
	new_socket = Accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&c);
	puts("Connection accepted")

	//Write response
	message = "Hello Client , I have received your connection. But I have to go now, bye\n";
	write(new_socket , message , strlen(message));
}

void clientCode(int family, int socketType, int packet_size) {
	int socket_desc;
	struct sockaddr_in server;

	socket_desc = Socket(AF_INET , SOCK_STREAM , 0);
	server.sin_addr.s_addr = inet_addr("192.168.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons(PORT);
	Connect(socket_desc, (struct sockaddr *)&server , sizeof(server))
	puts("Connected\n");
	//do transfer data
	message = "GET / HTTP/1.1\r\n\r\n";
	Send(socket_desc , message , strlen(message) , 0)
	puts("Client data Send\n");


	close(socket_desc);
	puts("Socket closed\n");
}

void doTest(int family, int socketType, int packet_size) {
	printf("starting test with family = %d, blocking=%d, packet_size=%d \n", family, socketType, packet_size);
	//start a server in separate process
	if(Fork() == 0) {
		serverCode(family, socketType, packet_size);
	}
	clientCode(family, socketType, packaet_size);
}

int main() {
	int families[] = {UNIX, INET};
	int connection_types[] = {BLOCKING, NONBLOCKING};
	int packet_sizes[] = {1024, 2048, 4096, 8192, 16384};
	int i,j,k;
	for(i = 0; i < NELEMS(families); ++i) {
		int family = families[i];
		for(j = 0; j < NELEMS(connection_types); ++j) {
			int connection_type = connection_type[j];
			for(k = 0; k < NELEMS(packet_sizes); ++ k) {
				int packet_size = packet_sizes[i];
				doTest(family, connection_type, packet_size);
			}
		}
	}
}