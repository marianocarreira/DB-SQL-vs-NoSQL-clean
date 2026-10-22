import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress

if (vars.getObject('mongoClient') == null) {
    String password = props.getProperty('mongo.password', '')
    char[] chars = password.toCharArray()
    try {
        def credential = MongoCredential.createCredential(
            props.getProperty('mongo.username'), props.getProperty('mongo.authDatabase'), chars)
        def options = MongoClientOptions.builder()
            .sslEnabled(Boolean.parseBoolean(props.getProperty('mongo.tls','true')))
            .sslInvalidHostNameAllowed(false)
            .connectTimeout(10000).serverSelectionTimeout(10000)
            .applicationName('DisposableDatabaseBenchmark').build()
        def client = new MongoClient(
            new ServerAddress(props.getProperty('mongo.host'),Integer.parseInt(props.getProperty('mongo.port'))),
            Collections.singletonList(credential), options)
        vars.putObject('mongoClient', client)
        vars.putObject('postsCollection', client.getDatabase(props.getProperty('mongo.database'))
            .getCollection(props.getProperty('mongo.collection','Posts')))
    } finally { Arrays.fill(chars, (char)0) }
}
