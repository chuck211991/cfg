package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.Guess;
import models.Review;
import models.Score;
import models.UserZ;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;

public class ReviewController extends Controller {
	@BodyParser.Of(BodyParser.Json.class)
	public static Result list() {
		return play.mvc.Results.TODO;
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result get(Long id) {
		return play.mvc.Results.TODO;
	}

	@SecureSocial.SecuredAction
	@BodyParser.Of(BodyParser.Json.class)
	public static Result post() {
		UserZ user = UserZ.findByIdentity((Identity) ctx().args
				.get(SecureSocial.USER_KEY));
		JsonNode json = request().body().asJson();
		Score score = Json.fromJson(json.get("score"), Score.class);
		Long guess_id = Json.fromJson(json.get("guess").get("id"), Long.class);
		Guess guess = Guess.find.byId(guess_id);

		Review review = new Review(user, guess, score);
        score.guess = guess;
        score.user = user;
        score.review = review;

		review.save();

        // Apply scales
        score.grammarScore *= 2;
        score.funScore *= 1;
        score.accuracyScore *= 3;

        score.save();

		// Reward the user monies for playing
		// Don't need to lock the DB
		// 20 monies per character?
		user.monies += review.guess.guess.length() * 20;
		user.save();

		return ok(review.toJson());
	}

	@BodyParser.Of(BodyParser.Json.class)
	public static Result put(Long id) {
		return play.mvc.Results.TODO;
	}

	@SecureSocial.SecuredAction
	@BodyParser.Of(BodyParser.Json.class)
	public static Result New() {
		UserZ user = UserZ.findByIdentity((Identity) ctx().args
				.get(SecureSocial.USER_KEY));
		// Pick a random guess
		Guess guess = Guess.getRandomGuess();

		Review review = new Review(user, guess, null);
		review.score = new Score(user, review, guess);

		return ok(review.toJson());
	}
}
