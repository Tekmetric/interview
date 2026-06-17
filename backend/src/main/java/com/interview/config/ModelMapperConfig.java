package com.interview.config;

import com.interview.dto.AlbumRefDto;
import com.interview.dto.ArtistListDto;
import com.interview.dto.ArtistRefDto;
import com.interview.dto.SongRefDto;
import com.interview.entity.Album;
import com.interview.entity.Artist;
import com.interview.entity.Song;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Use strict matching strategy to avoid ambiguous mappings
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Allow skipping null values during mapping
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.createTypeMap(Artist.class, ArtistListDto.class).addMappings(mapper -> {
            mapper.map(src -> src.getAlbums().size(), ArtistListDto::setAlbumCount);
            mapper.map(src -> src.getSongs().size(), ArtistListDto::setSongCount);
        });

        modelMapper.createTypeMap(Album.class, AlbumRefDto.class);
        modelMapper.createTypeMap(Song.class, SongRefDto.class);
        modelMapper.createTypeMap(Artist.class, ArtistRefDto.class);

        return modelMapper;
    }
}
