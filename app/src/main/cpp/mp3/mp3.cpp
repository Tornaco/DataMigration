#include <iostream>
#include <fstream>
#include <cstring>
#include <iomanip>
#include <vector>
#include "mp3.h"

using namespace std;

string MP3::get_name() {
    if (!this->name || !this->name->get_set())
        return "NAME NOT SET\n";
    char *temp;
    string str = "";

    temp = this->name->get_buf();
    for (int i = 0; i < this->name->get_size(); i++)
        str += (char) temp[i];

    str += "\n";
    return str;
}

string MP3::get_artist() {

    if (!this->artist || !this->artist->get_set())
        return "ARTIST NOT SET\n";
    char *temp;
    string str = "";

    temp = this->artist->get_buf();
    for (int i = 0; i < this->artist->get_size(); i++)
        str += (char) temp[i];

    str += "\n";
    return str;
}

string MP3::get_pic() {
    if (!this->pic || !this->pic->get_set())
        return "PICTURE NOT SET\n";
    char *temp;
    string str = "";

    temp = this->pic->get_buf();
    for (int i = 0; i < this->pic->get_size(); i++)
        str += (char) temp[i];

    return str;
}

int MP3::save_pic(std::string fp) {
    if (!this->pic || !this->pic->get_set())
        return -1;
    ofstream file(fp.c_str(), ios::out | ios::binary | ios::trunc);
    char *temp;

    temp = this->pic->get_buf();
    for (int i = 0; i < this->pic->get_size(); i++)
        file << (char) temp[i];

    file.close();
    return 0;
}

double MP3::get_len() {
    return this->len;
}

int MP3::get_bitrate() {
    return this->bitrate;
}

MP3::MP3() {
    this->name = NULL;
    this->artist = NULL;
    this->pic = NULL;
    this->len = 0;
    this->bitrate = 0;
}

MP3::MP3(std::string fp) {
    this->name = NULL;
    this->artist = NULL;
    this->pic = NULL;
    this->len = 0;
    this->parse_mp3(fp);
}


MP3::~MP3() {
    delete name;
    delete artist;
    delete pic;
}

//0 for success
int MP3::parse_mp3(std::string fp) {

    char c;
    vector<int> hold, temp;
    int match = 0, i = 0;

    ifstream file(fp.c_str(), ios::ate | ios::binary);
    if (!file) {
        cout << "Cannot open " << fp << "\n";
        return -1;
    }
    this->file_size = file.tellg();
    file.close();

    file.open(fp.c_str(), ios::in | ios::binary);
    if (!file) {
        cout << "Cannot open " << fp << "\n";
        return -1;
    }

    //checks to see if it is a ID3 MP3 format
    for (i = 0; i < 3; i++) {
        file.get(c);
        if (c != header[i]) {
            cout << "Not valid MP3 file\n";
            return -2;
        }
    }
    this->header_size = concat_size(file, 6, 4);
    //end checking

    //parse file into frames
    for (int j = 0; j < this->header_size; j++) {
        file.get(c);

        for (i = 0; i < (int) ((!match) ? 3 : hold.size()); i++) {
            if (id[((!match) ? i : hold.at(i))][match] == c)
                temp.push_back(((!match) ? i : hold.at(i)));
        }
        hold = temp;
        if (!hold.empty()) match++;
        else { match = 0; }

        if (match == 4) {
            switch (hold.at(0)) {
                case (0):
                    this->pic = new MP3FRAME(hold.at(0));
                    this->pic->set_size(
                            ((concat_size(file, ((int) file.tellg() + 2), 2)) - 16) * 4);
                    file.seekg((int) file.tellg() + 16);
                    this->pic->set_buf(file, this->pic->get_size() * 4);
                    break;

                case (1):
                    this->name = new MP3FRAME(hold.at(0));
                    this->name->set_size(concat_size(file, ((int) file.tellg() + 2), 2) - 4);
                    file.seekg((int) file.tellg() + 5);
                    this->name->set_buf(file, this->name->get_size());
                    break;

                case (2):
                    this->artist = new MP3FRAME(hold.at(0));
                    this->artist->set_size(concat_size(file, ((int) file.tellg() + 2), 2) - 4);
                    file.seekg((int) file.tellg() + 5);
                    this->artist->set_buf(file, this->artist->get_size());
                    break;
            }
            match = 0;
        }
        temp = vector<int>();

    }

    this->len = parse_mp3_frames(file);

    file.close();
    return 0;
}

double MP3::parse_mp3_frames(std::ifstream &file) {

    char c;
    int match = 0, count = 0, i = 0, j = 0, k = 0;
    uint8_t id = 0, layer = 0, bitrate = 0;
    double ave_bitrate = 0;

    vector<int> bitrates;
    int bitrate_list[450];

    memset(bitrate_list, 0, 450 * sizeof(int));

    while (file.get(c)) {
        if (!match && (uint8_t) c == 0xFF) { match++; }
        else if (match == 1 && !(((uint8_t) c ^ 0xF0) & 0xD0)) {
            match = 0;
            count++;
            id = ((((uint8_t) c) & 0x1F) >> 3);
            layer = ((((uint8_t) c) & 0x07) >> 1);
            if (file.get(c)) {
                bitrate = ((((uint8_t) c) & 0xF0) >> 4);
                bitrates.push_back(bitrate_lookup(id, layer, bitrate));
            }
            else break;
        }
        else match = 0;
    }

    for (i = 0; i < (int) bitrates.size(); i++)
        bitrate_list[bitrates.at(i)]++;

    for (i = 1; i < 450; i++)
        for (j = 0; j < bitrate_list[i]; j++)
            ave_bitrate += i;

    ave_bitrate /= (bitrates.size() - bitrate_list[0]);

    for (i = 1; i < 450; i++)
        if (bitrate_list[i] > j) {
            k = i;
            j = bitrate_list[i];
        }

    this->bitrate = k;
    return ((double) (((this->file_size - this->header_size) * 8)) /
            (((double) ave_bitrate) * 1000));
}

int MP3::bitrate_lookup(uint8_t id, uint8_t layer, uint8_t bitrate_nibble) {

    int bitmap[16][5] = {
            {0,   0,   0,   0,   0},
            {32,  32,  32,  32,  8},
            {64,  48,  40,  48,  16},
            {96,  56,  48,  56,  24},
            {128, 64,  56,  64,  32},
            {160, 80,  64,  80,  40},
            {192, 96,  80,  96,  48},
            {224, 112, 96,  112, 56},
            {256, 128, 112, 128, 64},
            {288, 160, 128, 144, 80},
            {320, 192, 160, 160, 96},
            {352, 224, 192, 176, 112},
            {384, 256, 224, 192, 128},
            {416, 320, 256, 224, 144},
            {448, 384, 320, 256, 160},
            {0,   0,   0,   0,   0}};

    switch (id) {
        case (0x00): //Version 2 or 2.5
        case (0x01):
            switch (layer) {
                case (0x03):
                    return bitmap[bitrate_nibble][3];
                case (0x02):
                case (0x01):
                    return bitmap[bitrate_nibble][4];
                default:
                    return 0;
            }
            break;
        case (0x03): //Version 1
            switch (layer) {
                case (0x03):
                    return bitmap[bitrate_nibble][0];
                case (0x02):
                    return bitmap[bitrate_nibble][1];
                case (0x01):
                    return bitmap[bitrate_nibble][2];
                default:
                    return 0;
            }
            break;
        default:
            return 0;

    }
    return 0;
}

int MP3::concat_size(std::ifstream &file, int pos, int bytes) {

    int size = 0;
    char c;
    file.seekg(pos);
    for (int i = 0; i < bytes; i++) {
        file.get(c);
        size += (int) (((uint8_t) c) << (((bytes - 1) * 8) - (i * 8)));
    }
    return size;
}
