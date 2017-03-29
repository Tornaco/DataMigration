#ifndef MP3FRAME_H
#define MP3FRAME_H

#include<string>
#include<fstream>

class MP3FRAME{
  private:
    std::string name;
    int size, set;
    char *buf;
  public:
    MP3FRAME(int);
    ~MP3FRAME(void);

    void set_size(int);
    void set_buf(std::ifstream&, int);

    std::string get_name(void);
    int get_size(void);
    int get_set(void);
    char* get_buf(void);
};

const char id[3][4] = {
    {'A', 'P', 'I', 'C'},
    {'T', 'P', 'E', '2'},
    {'T', 'I', 'T', '2'}};

const char header[3] = {'I', 'D', '3'};

#endif


