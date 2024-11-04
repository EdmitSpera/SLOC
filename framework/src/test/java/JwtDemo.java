import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Test;

public class JwtDemo {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzbG9jIn0.8viwDJenv51nair0xQ1c12zeosVLo8L2IrGTZMQuWx8";
    String JwtKey = "sloc-key";
    DecodedJWT decodedJWT;

    @Test
    public void verifyToken(){
        try {
            Algorithm algorithm = Algorithm.HMAC256(JwtKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("sloc")
                    .build();

            decodedJWT = verifier.verify(token);
            System.out.println();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (JWTVerificationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createToken(){
        Algorithm algorithm = Algorithm.HMAC256(JwtKey);

        token = JWT.create()
                .withIssuer("sloc")
                .sign(algorithm);
        System.out.println(token);
    }
}
