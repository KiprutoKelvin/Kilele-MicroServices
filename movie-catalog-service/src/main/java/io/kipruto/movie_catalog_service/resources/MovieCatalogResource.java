package io.kipruto.movie_catalog_service.resources;

import com.netflix.discovery.DiscoveryClient;
import io.kipruto.movie_catalog_service.models.CatalogItem;
import io.kipruto.movie_catalog_service.models.Movie;
import io.kipruto.movie_catalog_service.models.Rating;
import io.kipruto.movie_catalog_service.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private DiscoveryClient discoveryClient;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {


        //RestClient
        // Assume
        // get all rated movie IDs
        // for each movie ID call movie_info_service
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), "Old times", rating.getRating());
                })
            .collect(Collectors.toList());
    }
}
