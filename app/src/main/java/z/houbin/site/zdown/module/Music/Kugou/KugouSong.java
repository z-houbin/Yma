package z.houbin.site.zdown.module.Music.Kugou;

import java.util.List;

public class KugouSong {

    /**
     * status : 1
     * err_code : 0
     * data : {"hash":"6032A242007981C4CCDF9FD3DD5D9318","timelength":270000,"filesize":4321478,"audio_name":"罗之豪 - 残酷月光","have_album":0,"album_name":"未知专辑","album_id":0,"img":"http://singerimg.kugou.com/uploadpic/softhead/400/20170731/20170731110100828428.jpg","have_mv":0,"video_id":0,"author_name":"罗之豪","song_name":"残酷月光","lyrics":"[00:00.10]罗之豪 - 残酷月光\r\n[00:10.30]词：向月娥\r\n[00:12.56]曲：陈小霞\r\n[00:14.49]让我爱你\r\n[00:25.06]然后把我抛弃\r\n[00:28.51]我只要出发\r\n[00:32.27]不要目的\r\n[00:36.04]我会一直想你\r\n[00:40.06]忘记了呼吸\r\n[00:43.81]孤独到底\r\n[00:47.42]让我昏迷\r\n[00:51.18]如果恨你\r\n[00:54.63]就能不忘记你\r\n[00:57.74]所有的面目\r\n[01:01.66]我都不抗拒\r\n[01:05.83]如果不够悲伤\r\n[01:10.01]就无法飞翔\r\n[01:13.41]可没有梦想\r\n[01:16.46]何必远方\r\n[01:20.88]我一直都在流浪\r\n[01:24.34]可我不曾见过海洋\r\n[01:27.90]我以为的遗忘\r\n[01:31.36]原来躺在你手上\r\n[01:34.89]我努力微笑坚强\r\n[01:38.70]寂寞筑成一道围墙\r\n[01:42.67]也抵不过夜里\r\n[01:45.87]最温柔的月光\r\n[02:00.88]如果恨你\r\n[02:14.15]就能不忘记你\r\n[02:17.97]所有的面目\r\n[02:20.83]我都不抗拒\r\n[02:25.14]如果不够悲伤\r\n[02:28.79]就无法飞翔\r\n[02:32.19]可没有梦想\r\n[02:36.21]何必远方\r\n[02:40.17]我一直都在流浪\r\n[02:43.63]可我不曾见过海洋\r\n[02:47.29]我以为的遗忘\r\n[02:49.22]原来躺在你手上\r\n[02:53.80]我努力微笑坚强\r\n[02:58.12]寂寞筑成一道围墙\r\n[03:01.68]也抵不过夜里\r\n[03:03.71]最温柔的月光\r\n[03:15.11]我一直都在流浪\r\n[03:33.62]可我不曾见过海洋\r\n[03:37.07]我以为的遗忘\r\n[03:40.28]原来躺在你手上\r\n[03:44.86]我努力微笑坚强\r\n[03:47.96]寂寞筑成一道围墙\r\n[03:51.77]也抵不过夜里\r\n[03:54.97]最温柔的月光\r\n","author_id":"576675","privilege":0,"privilege2":"0","play_url":"http://fs.w.kugou.com/201804171651/749fc8330a3a6d73788a0dbb04ae2804/G106/M06/1D/1F/qg0DAFmosoiAZXH1AEHwxjJTCJM185.mp3","authors":[{"is_publish":"1","author_id":"576675","avatar":"20170731110100828428.jpg","author_name":"罗之豪"}],"bitrate":128}
     */

    private int status;
    private int err_code;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * hash : 6032A242007981C4CCDF9FD3DD5D9318
         * timelength : 270000
         * filesize : 4321478
         * audio_name : 罗之豪 - 残酷月光
         * have_album : 0
         * album_name : 未知专辑
         * album_id : 0
         * img : http://singerimg.kugou.com/uploadpic/softhead/400/20170731/20170731110100828428.jpg
         * have_mv : 0
         * video_id : 0
         * author_name : 罗之豪
         * song_name : 残酷月光
         * lyrics : [00:00.10]罗之豪 - 残酷月光
         [00:10.30]词：向月娥
         [00:12.56]曲：陈小霞
         [00:14.49]让我爱你
         [00:25.06]然后把我抛弃
         [00:28.51]我只要出发
         [00:32.27]不要目的
         [00:36.04]我会一直想你
         [00:40.06]忘记了呼吸
         [00:43.81]孤独到底
         [00:47.42]让我昏迷
         [00:51.18]如果恨你
         [00:54.63]就能不忘记你
         [00:57.74]所有的面目
         [01:01.66]我都不抗拒
         [01:05.83]如果不够悲伤
         [01:10.01]就无法飞翔
         [01:13.41]可没有梦想
         [01:16.46]何必远方
         [01:20.88]我一直都在流浪
         [01:24.34]可我不曾见过海洋
         [01:27.90]我以为的遗忘
         [01:31.36]原来躺在你手上
         [01:34.89]我努力微笑坚强
         [01:38.70]寂寞筑成一道围墙
         [01:42.67]也抵不过夜里
         [01:45.87]最温柔的月光
         [02:00.88]如果恨你
         [02:14.15]就能不忘记你
         [02:17.97]所有的面目
         [02:20.83]我都不抗拒
         [02:25.14]如果不够悲伤
         [02:28.79]就无法飞翔
         [02:32.19]可没有梦想
         [02:36.21]何必远方
         [02:40.17]我一直都在流浪
         [02:43.63]可我不曾见过海洋
         [02:47.29]我以为的遗忘
         [02:49.22]原来躺在你手上
         [02:53.80]我努力微笑坚强
         [02:58.12]寂寞筑成一道围墙
         [03:01.68]也抵不过夜里
         [03:03.71]最温柔的月光
         [03:15.11]我一直都在流浪
         [03:33.62]可我不曾见过海洋
         [03:37.07]我以为的遗忘
         [03:40.28]原来躺在你手上
         [03:44.86]我努力微笑坚强
         [03:47.96]寂寞筑成一道围墙
         [03:51.77]也抵不过夜里
         [03:54.97]最温柔的月光

         * author_id : 576675
         * privilege : 0
         * privilege2 : 0
         * play_url : http://fs.w.kugou.com/201804171651/749fc8330a3a6d73788a0dbb04ae2804/G106/M06/1D/1F/qg0DAFmosoiAZXH1AEHwxjJTCJM185.mp3
         * authors : [{"is_publish":"1","author_id":"576675","avatar":"20170731110100828428.jpg","author_name":"罗之豪"}]
         * bitrate : 128
         */

        private String hash;
        private int timelength;
        private int filesize;
        private String audio_name;
        private int have_album;
        private String album_name;
        private int album_id;
        private String img;
        private int have_mv;
        private int video_id;
        private String author_name;
        private String song_name;
        private String lyrics;
        private String author_id;
        private int privilege;
        private String privilege2;
        private String play_url;
        private int bitrate;
        private List<AuthorsBean> authors;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public int getTimelength() {
            return timelength;
        }

        public void setTimelength(int timelength) {
            this.timelength = timelength;
        }

        public int getFilesize() {
            return filesize;
        }

        public void setFilesize(int filesize) {
            this.filesize = filesize;
        }

        public String getAudio_name() {
            return audio_name;
        }

        public void setAudio_name(String audio_name) {
            this.audio_name = audio_name;
        }

        public int getHave_album() {
            return have_album;
        }

        public void setHave_album(int have_album) {
            this.have_album = have_album;
        }

        public String getAlbum_name() {
            return album_name;
        }

        public void setAlbum_name(String album_name) {
            this.album_name = album_name;
        }

        public int getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(int album_id) {
            this.album_id = album_id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getHave_mv() {
            return have_mv;
        }

        public void setHave_mv(int have_mv) {
            this.have_mv = have_mv;
        }

        public int getVideo_id() {
            return video_id;
        }

        public void setVideo_id(int video_id) {
            this.video_id = video_id;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getSong_name() {
            return song_name;
        }

        public void setSong_name(String song_name) {
            this.song_name = song_name;
        }

        public String getLyrics() {
            return lyrics;
        }

        public void setLyrics(String lyrics) {
            this.lyrics = lyrics;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public int getPrivilege() {
            return privilege;
        }

        public void setPrivilege(int privilege) {
            this.privilege = privilege;
        }

        public String getPrivilege2() {
            return privilege2;
        }

        public void setPrivilege2(String privilege2) {
            this.privilege2 = privilege2;
        }

        public String getPlay_url() {
            return play_url;
        }

        public void setPlay_url(String play_url) {
            this.play_url = play_url;
        }

        public int getBitrate() {
            return bitrate;
        }

        public void setBitrate(int bitrate) {
            this.bitrate = bitrate;
        }

        public List<AuthorsBean> getAuthors() {
            return authors;
        }

        public void setAuthors(List<AuthorsBean> authors) {
            this.authors = authors;
        }

        public static class AuthorsBean {
            /**
             * is_publish : 1
             * author_id : 576675
             * avatar : 20170731110100828428.jpg
             * author_name : 罗之豪
             */

            private String is_publish;
            private String author_id;
            private String avatar;
            private String author_name;

            public String getIs_publish() {
                return is_publish;
            }

            public void setIs_publish(String is_publish) {
                this.is_publish = is_publish;
            }

            public String getAuthor_id() {
                return author_id;
            }

            public void setAuthor_id(String author_id) {
                this.author_id = author_id;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getAuthor_name() {
                return author_name;
            }

            public void setAuthor_name(String author_name) {
                this.author_name = author_name;
            }
        }
    }
}
