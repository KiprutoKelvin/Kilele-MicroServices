package io.kipruto.ratings_data_service.resources;

import io.kipruto.ratings_data_service.models.Rating;
import io.kipruto.ratings_data_service.models.UserRating;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/ratingsdata")
public class RatingsResource {

    @RequestMapping("/{movieId}")
    public Rating getRating(@PathVariable("movieId") String movieId) {
        return new Rating(movieId, 4);
    }

    @RequestMapping("users/{userId}")
    public UserRating getUserRating(@PathVariable("userId") String userId) { // Object Wrapper Recommended
        List<Rating> ratings = Arrays.asList( // Deserialization Challenge will occur-> Not Recommended
                new Rating("235456", 4),
                new Rating("780989", 5)
        );

        UserRating userRating = new UserRating();
        userRating.setUserRating(ratings);
        return userRating;
    }
}