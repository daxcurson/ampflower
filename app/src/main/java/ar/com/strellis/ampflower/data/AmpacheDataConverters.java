package ar.com.strellis.ampflower.data;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import ar.com.strellis.ampflower.data.model.Album;
import ar.com.strellis.ampflower.data.model.Artist;
import ar.com.strellis.ampflower.data.model.Genre;
import ar.com.strellis.ampflower.data.model.Tag;

/**
 * Converts between the database types and those used in the data structures
 */
public class AmpacheDataConverters
{
    @TypeConverter
    public static int fromArtist(Artist artist)
    {
        if(artist==null) {
            return 0;
        }
        return artist.getId();
    }
    @TypeConverter
    public static Artist toArtist(int artistId)
    {
        Artist a=new Artist();
        a.setId(artistId);
        return a;
    }
    @TypeConverter
    public static int fromAlbum(Album album)
    {
        return album.getId();
    }
    @TypeConverter
    public static Album toAlbum(int albumId)
    {
        Album a=new Album();
        a.setId(albumId);
        return a;
    }
    @TypeConverter
    public static String fromTagList(List<Tag> tagList)
    {
        Gson gson = new Gson();
        return gson.toJson(tagList);
    }
    @TypeConverter
    public static List<Tag> toTagList(String tagList)
    {
        Type listType = new TypeToken<LinkedList<Tag>>() {}.getType();
        return new Gson().fromJson(tagList, listType);
    }
    @TypeConverter
    public static String fromGenreList(List<Genre> genreList)
    {
        Gson gson=new Gson();
        return gson.toJson(genreList);
    }
    @TypeConverter
    public static List<Genre> toGenreList(String genreList)
    {
        Type listType = new TypeToken<LinkedList<Genre>>() {}.getType();
        return new Gson().fromJson(genreList, listType);
    }
}
