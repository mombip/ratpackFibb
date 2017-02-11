package fibb;

import ratpack.exec.Promise;
import ratpack.http.client.HttpClient;
import ratpack.http.client.HttpClientSpec;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.RatpackServer;

import java.net.URI;
import java.time.Duration;

/**
 * Created by marek on 2017-02-11.
 */
public class Fibb {

    public static final String PREFIX = "fibb";
    public static final String HOST = "http://locaahost:5050/"+PREFIX+"/";

    public static void main(String[] arg) throws Exception{
        HttpClient httpClient = HttpClient.of(trs -> trs.readTimeout(Duration.ofMinutes(1)));

        RatpackServer.start(server -> server
                .serverConfig(
                        sc -> sc
                                .port(5050)
                        .threads(1)
                )
                .handlers(chain -> chain.
                prefix("fibb", fib -> fib.get(":n", ctx -> {
                    final long n = Long.parseLong(ctx.getAllPathTokens().get("n"));
                    if(n<2){
                        ctx.render("1");
                    }else{

                        Promise<Long> f1 = httpClient.get(
                                URI.create(HOST + (n - 1)))
                                .map(result -> Long.parseLong(result.getBody().getText()));
                        Promise<Long> f2 = httpClient.get( URI.create(HOST + (n - 2)))
                                .map(result -> Long.parseLong(result.getBody().getText()));

                        f1.then(v1 -> f2.then(v2 -> ctx.render(  String.valueOf(v1+v2))));

                    }

                }))
        ));

    }
}
