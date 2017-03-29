#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <stdio.h>
#include <stdlib.h>
#include "mp3frame.h"

using namespace std;

MP3FRAME::MP3FRAME(int id_num){
  string str = "";
  if(!((unsigned int)id_num < 3)){
    cout << "BAD ID add in exception\n";
    return;
  }
  for(int i=0; i < 4; i++){str += id[id_num][i];}
  this->set = 0;
  this->size = 0;
  this->name = str;
  this->buf = NULL;
}

MP3FRAME::~MP3FRAME(){
  if(this->set)
    free(this->buf);
}

void MP3FRAME::set_size(int s){
  this->size = s;
}

void MP3FRAME::set_buf(std::ifstream& file, int l){
  char c;
  this->buf = (char*) malloc(l * sizeof(char));
  
  for(int i = 0; i < l; i++){
    file.get(c);
    buf[i] = c;
  }
  
  this->set = 1;
}

string MP3FRAME::get_name(){
  return this->name;
}

int MP3FRAME::get_size(){
  return this->size;
}

char* MP3FRAME::get_buf(){
  return this->buf;
}

int MP3FRAME::get_set(){
  return this->set;
}
