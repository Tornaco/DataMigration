#ifndef MP3_H
#define MP3_H

#include <string>
#include <stdint.h>
#include "mp3frame.h"

class MP3{
  private:
    MP3FRAME *name, *artist, *pic;
    double len; //in sec
    int bitrate, file_size, header_size;
    int concat_size(std::ifstream&, int, int);
    double parse_mp3_frames(std::ifstream&);
    int bitrate_lookup(uint8_t, uint8_t, uint8_t);
  public: 
    MP3();
    MP3(std::string);
    ~MP3();
    int parse_mp3(std::string);
    std::string get_name(void);
    std::string get_artist(void);
    std::string get_pic(void);
    int save_pic(std::string);
    double get_len(void);
    int get_bitrate(void);
};



#endif


