// Validation-only setup sampler. No database connections are created here.
def required = { String key ->
    String value = props.getProperty(key, '')
    if (!value.trim()) throw new IllegalArgumentException('Required configuration missing: ' + key)
    value
}
def positive = { String key, int limit ->
    int value = Integer.parseInt(required(key))
    if (value < 1 || value > limit) throw new IllegalArgumentException('Configuration out of range: ' + key)
}
try {
    if (required('benchmark.targetConfirmed') != 'DISPOSABLE_DATABASES')
        throw new IllegalArgumentException('Explicit disposable-target confirmation is required')
    String profile = required('benchmark.profile')
    if (!(profile in ['local', 'remote'])) throw new IllegalArgumentException('Unknown benchmark profile')
    String url = required('sql.url')
    if (!(url.startsWith('jdbc:sqlserver://'))) throw new IllegalArgumentException('Expected SQL Server JDBC URL')
    Map fields = [:]
    url.split(';').drop(1).findAll { it.contains('=') }.each {
        def pair = it.split('=', 2); String k = pair[0].trim().toLowerCase()
        if (fields.containsKey(k)) throw new IllegalArgumentException('Duplicate JDBC option')
        fields[k] = pair[1].trim()
    }
    if (fields.keySet().any { it in ['user', 'username', 'password', 'pwd'] })
        throw new IllegalArgumentException('Keep JDBC credentials in separate properties')
    if (!fields['databasename']) throw new IllegalArgumentException('Explicit SQL databaseName is required')
    if (!(fields['encrypt'] in ['true','false'])) throw new IllegalArgumentException('Set JDBC encrypt explicitly')
    if (fields['trustservercertificate'] != 'false') throw new IllegalArgumentException('Certificate validation must remain enabled')
    String sqlHost = url.substring('jdbc:sqlserver://'.length()).split(';')[0].split(':')[0].toLowerCase()
    String mongoHost = required('mongo.host').toLowerCase()
    def loopback = { String host -> host in ['localhost','127.0.0.1'] }
    if (profile == 'local' && (!loopback(sqlHost) || !loopback(mongoHost)))
        throw new IllegalArgumentException('Local profile requires loopback hosts')
    if (!(required('mongo.tls') in ['true','false'])) throw new IllegalArgumentException('Set mongo.tls explicitly')
    if (profile == 'remote' && (fields['encrypt'] != 'true' || props.getProperty('mongo.tls') != 'true'))
        throw new IllegalArgumentException('Remote profile requires TLS for both engines')
    if (!(required('benchmark.allowWrites') in ['true','false'])) throw new IllegalArgumentException('Set benchmark.allowWrites explicitly')
    if (required('sql.username').equalsIgnoreCase('sa')) throw new IllegalArgumentException('Use a database-scoped test user instead of sa')
    ['sql.password','mongo.username','mongo.password','mongo.database','mongo.authDatabase','mongo.collection'].each { required(it) }
    positive('mongo.port',65535); positive('benchmark.threads',100); positive('benchmark.loops',1000)
    positive('benchmark.rampSeconds',86400); positive('benchmark.writeRows',500000)
    SampleResult.setSuccessful(true)
    SampleResult.setResponseData('Configuration validated. No credentials logged.', 'UTF-8')
} catch (Exception ex) {
    // Do not include exception text or values: parsers may echo credential-bearing inputs.
    SampleResult.setSuccessful(false)
    SampleResult.setResponseCode('CONFIGURATION_REJECTED')
    SampleResult.setResponseMessage('Configuration rejected; consult config/README.md')
    SampleResult.setStopTestNow(true)
    ctx.getEngine().stopTest(true)
}
