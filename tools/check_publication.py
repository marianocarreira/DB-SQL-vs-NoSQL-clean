"""Offline checks; no credentials are read and no services are contacted."""
from pathlib import Path
import re,sys,xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[1]
errors=[]
for p in ROOT.rglob('*'):
    if not p.is_file() or '.git' in p.relative_to(ROOT).parts: continue
    rel=p.relative_to(ROOT).as_posix()
    if p.name.endswith('.local.properties') or rel.startswith(('results/raw/','results/reports/')): continue
    if p.suffix.lower() not in {'.md','.jmx','.sql','.mongo','.groovy','.properties','.py','.yml','.csv','.toml'} and p.name not in {'.gitignore','.gitattributes'}:
        errors.append(rel+': unreviewed file type'); continue
    s=p.read_text(encoding='utf-8-sig')
    if re.search(r'(?i)[A-Z]:[\\/](?:Users|Documents and Settings)[\\/]',s): errors.append(rel+': personal absolute path')
    if re.search(r'(?i)(?:mongodb(?:\+srv)?|https?)://[^\s/]+:[^\s/@]+@',s): errors.append(rel+': embedded URL credentials')
    if p.suffix=='.properties':
        for line in s.splitlines():
            if re.match(r'^\s*(?:sql|mongo)\.password\s*=\s*\S+',line): errors.append(rel+': nonempty published password')
    if p.suffix=='.groovy' and re.search(r'(?i)\bpassword\s*=\s*[\x22\x27][^\x22\x27]+[\x22\x27]',s): errors.append(rel+': password literal')
r=ET.parse(ROOT/'Jmeter/Jmeter_SQLvsMongo.jmx').getroot()
def val(e,n): return next(x.text or '' for x in e.iter() if x.attrib.get('name')==n)
sql=next(r.iter('JDBCDataSource'))
for n,k in [('username','sql.username'),('password','sql.password'),('dbUrl','sql.url')]:
    if val(sql,n)!='${__P('+k+',)}': errors.append('JDBC property not externalized: '+n)
pt=r.find('hashTree').find('hashTree')
if any(e.tag=='JSR223PreProcessor' for e in pt): errors.append('Global connection preprocessor would execute before validation')
nodes=list(pt); setup_i=next(i for i,e in enumerate(nodes) if e.tag=='SetupThreadGroup'); main_i=next(i for i,e in enumerate(nodes) if e.tag=='ThreadGroup')
if setup_i>=main_i: errors.append('Validation must precede main workload')
parents={ch:p for p in r.iter() for ch in p}; gated=0
for e in r.iter():
    if e.tag not in ('JDBCSampler','JSR223Sampler') or not re.match(r'^(INSERT|UPDATE|DELETE)\s',e.attrib.get('testname',''),re.I): continue
    parent=parents[e]; owner=parents[parent]
    kids=list(owner); at=kids.index(parent)
    if at<1 or kids[at-1].tag!='IfController' or val(kids[at-1],'IfController.condition')!='${__P(benchmark.allowWrites,false)}':
        errors.append('Ungated write sampler')
    gated+=1
if gated!=6: errors.append('Expected six gated write samplers')
if any(val(e,'TestPlan.user_define_classpath') for e in r.iter('TestPlan')): errors.append('Personal driver classpath still present')
for e in r.iter('stringProp'):
    if e.attrib.get('name')=='filename' and e.text and not (ROOT/e.text).is_file(): errors.append('Missing external script: '+e.text)
    if e.attrib.get('name')=='script' and e.text and 'new MongoClient(' in e.text: errors.append('Embedded connection escaped externalization')
if any(e.attrib.get('enabled')!='false' for e in r.iter('ResultCollector')): errors.append('GUI listener enabled')
if errors:
    print('\n'.join(errors));sys.exit(1)
print('Publication checks passed: external config, validation order, six write gates, file policy and paths.')
