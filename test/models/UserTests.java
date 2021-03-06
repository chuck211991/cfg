package models;

import org.junit.*;

import com.avaje.ebean.Ebean;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import play.test.*;
import securesocial.core.IdentityId;
import securesocial.core.SocialUser;
import static play.test.Helpers.*;

public class UserTests extends WithApplication {
	@Before
	public void setUp() {
		start(fakeApplication(inMemoryDatabase()));
	}

	@Test
	public void testUserPasswordNotPlainText() {
		String password = "myTopS3cretP@$sw0rd!1 With a space";
		User u = new User("user@user.com", "user", null, password);
		assertThat(u.password_hash, is(not(password)));
	}

	@Test
	public void testCanCreateUser() {
		String password = "myTopS3cretP@$sw0rd!1 With a space";
		User u = new User("user@user.com", "user", null, password);
		int initial_user = User.find.findRowCount();
		Ebean.save(u);

		assertThat(User.find.findRowCount(), is(initial_user + 1));
	}

	@Test
	public void testCanFindUserByIdentity() {
		SocialUser i = new SocialUser(new IdentityId("testUserId",
				"testProvider"), "Test", "User", "Test user", null, null, null,
				null, null, null);
		User u = new User(i);
		Ebean.save(u);

		User n = User.findByIdentity(i);

		assertThat(n, is(u));
	}
}
