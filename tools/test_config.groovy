// Offline validation tests: all credentials below are synthetic, no connections are made.
class SampleStub {
    boolean successful = false
    boolean stopped = false
    String code = ''
    void setSuccessful(boolean v) { successful=v }
    void setResponseData(String s, String encoding) { }
    void setResponseCode(String s) { code=s }
    void setResponseMessage(String s) { }
    void setStopTestNow(boolean v) { stopped=v }
}
def root = new File(args ? args[0] : '.').canonicalFile
def base = [
    'benchmark.profile':'local', 'benchmark.targetConfirmed':'DISPOSABLE_DATABASES',
    'benchmark.allowWrites':'false', 'benchmark.writeRows':'1000', 'benchmark.threads':'1',
    'benchmark.loops':'2', 'benchmark.rampSeconds':'1',
    'sql.url':'jdbc:sqlserver://localhost:1433;databaseName=fixture_lab;encrypt=true;trustServerCertificate=false;',
    'sql.username':'fixture_user', 'sql.password':'test-fixture',
    'mongo.host':'localhost','mongo.port':'27017','mongo.database':'fixture_lab',
    'mongo.authDatabase':'fixture_lab','mongo.collection':'Posts','mongo.username':'fixture_user',
    'mongo.password':'test-fixture','mongo.tls':'true'
]
def cases = [
    ['configured loopback', [:], true],
    ['unconfirmed target', ['benchmark.targetConfirmed':''], false],
    ['missing password', ['sql.password':''], false],
    ['server administrator', ['sql.username':'sa'], false],
    ['remote plaintext SQL', ['benchmark.profile':'remote','sql.url':base['sql.url'].replace('encrypt=true','encrypt=false')], false],
    ['remote plaintext MongoDB', ['benchmark.profile':'remote','mongo.tls':'false'], false],
    ['local remote MongoDB', ['mongo.host':'db.example.invalid'], false],
    ['local remote SQL', ['sql.url':base['sql.url'].replace('localhost','db.example.invalid')], false],
    ['certificate bypass', ['sql.url':base['sql.url'].replace('trustServerCertificate=false','trustServerCertificate=true')], false],
    ['duplicate JDBC option', ['sql.url':base['sql.url']+'encrypt=false;'], false],
    ['explicit local plaintext', ['sql.url':base['sql.url'].replace('encrypt=true','encrypt=false'),'mongo.tls':'false'], true],
    ['explicit write opt-in', ['benchmark.allowWrites':'true'], true],
    ['malformed write flag', ['benchmark.allowWrites':'TRUE'], false],
    ['invalid port', ['mongo.port':'0'], false],
    ['too many write rows', ['benchmark.writeRows':'500001'], false],
    ['configured remote TLS', ['benchmark.profile':'remote','mongo.host':'db.example.invalid','sql.url':base['sql.url'].replace('localhost','sql.example.invalid')], true]
]
cases.each { entry ->
    def properties = new Properties(); (base + entry[1]).each { k,v -> properties.setProperty(k,v) }
    def result = new SampleStub()
    def engine = new Expando(stopTest: { boolean immediate -> })
    def context = new Expando(getEngine: { -> engine })
    def binding = new Binding([props:properties, SampleResult:result, ctx:context])
    new GroovyShell(binding).evaluate(new File(root,'config/validate.groovy'))
    assert result.successful == entry[2] : entry[0]
    assert result.stopped == !entry[2] : entry[0]
    if (!entry[2]) assert result.code == 'CONFIGURATION_REJECTED' : entry[0]
}
println('Passed '+cases.size()+' offline configuration cases. No database connections made.')
