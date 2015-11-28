---
-- #%L
-- cloudconductor-server
-- %%
-- Copyright (C) 2013 - 2014 Cinovo AG
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
SET DATABASE REFERENTIAL INTEGRITY FALSE;

INSERT INTO cloudconductor.package VALUES (1, 'postgresql92', 'Auto-generated from repository update on 2013-09-04 14:20:07.');
INSERT INTO cloudconductor.package VALUES (2, 'postgresql92-libs', 'Auto-generated from repository update on 2013-09-04 14:20:07.');
INSERT INTO cloudconductor.package VALUES (3, 'nginx', 'Auto-generated from repository update on 2013-09-04 14:20:08.');
INSERT INTO cloudconductor.package VALUES (4, 'jdk', 'Auto-generated from repository update on 2013-09-04 14:20:08.');
INSERT INTO cloudconductor.package VALUES (5, 'postgresql92-server', 'Auto-generated from repository update on 2013-09-04 14:20:09.');
INSERT INTO cloudconductor.package VALUES (6, 'nodejs', 'Auto-generated from repository update on 2013-09-04 14:20:09.');

INSERT INTO cloudconductor.auditlog VALUES (1, 1385544948453, 'Admin', 'Added template dev', 1, 0, null, null, null);
INSERT INTO cloudconductor.auditlog VALUES (2, 1390125583142, 'Admin', 'Added service service1', 6, 0, null, null, null);
INSERT INTO cloudconductor.auditlog VALUES (3, 1390125802050, 'Admin', 'Modified file file1', 5, 0, null, null, null);
INSERT INTO cloudconductor.auditlog VALUES (4, 1390125817657, 'Admin', 'Modified key null', 3, 0, null, null, null);

INSERT INTO cloudconductor.configvalues VALUES (4, 'GLOBAL', '', 'cloudconductor.username', 'admin', 0, false, 4);
INSERT INTO cloudconductor.configvalues VALUES (14, 'GLOBAL', '', 'syslog.host', 'localhost', 0, false, 14);
INSERT INTO cloudconductor.configvalues VALUES (17, 'GLOBAL', '', 'syslog.port', '514', 0, false, 17);
INSERT INTO cloudconductor.configvalues VALUES (18, 'GLOBAL', '', 'syslog.level', 'INFO', 0, false, 18);
INSERT INTO cloudconductor.configvalues VALUES (26, 'GLOBAL', '', 'syslog.facility', 'LOCAL1', 0, false, 26);
INSERT INTO cloudconductor.configvalues VALUES (19, 'GLOBAL', NULL, 'logger.loggly', 'false', 0, false, 19);
INSERT INTO cloudconductor.configvalues VALUES (13, 'GLOBAL', NULL, 'cloudconductor.password', 'password', 0, false, 13);
INSERT INTO cloudconductor.configvalues VALUES (67, 'GLOBAL', 'service1', 'logger.loggly', 'true', 0, false, 67);
INSERT INTO cloudconductor.configvalues VALUES (68, 'GLOBAL', 'service1', 'loggly.tags', 'foo', 0, false, 68);

INSERT INTO cloudconductor.file VALUES (1, 'file1', '/root/foo', 'root', 'root', '755', false, false, '59cc417c3e8e463d2d2bdac8743ac04c', NULL, 0, false, 1);

INSERT INTO cloudconductor.filedata VALUES (1, 1, 'Testfile for root',0, false, 1);

INSERT INTO cloudconductor.mappingfiletemplate VALUES (1, 1, 1);

INSERT INTO cloudconductor.mappingtemplatesshkey VALUES (1, 1, 1);

INSERT INTO cloudconductor.service VALUES (1, 'postgresql-9.2', 'postgresql-9.2', 'postgresql-9.2');
INSERT INTO cloudconductor.service VALUES (2, 'nginx', 'nginx', 'nginx');
INSERT INTO cloudconductor.service VALUES (3, 'service1', 'service1', 'service1');

INSERT INTO cloudconductor.sshkey VALUES (1, 'SSH key content', 'foobar');

INSERT INTO cloudconductor.template VALUES (1, 'dev', 'dev', false, true);


INSERT INTO cloudconductor.packageservergroup VALUES (1, 'TESTREPO', '1');
INSERT INTO cloudconductor.packageservergroup VALUES (2, 'Ein etwas anderes REPO', '1');
INSERT INTO cloudconductor.packageservergroup VALUES (3, 'CloudConductor', '1');

INSERT INTO cloudconductor.packageserver (id, description, path, servergroupid, indexertype, providertype, basepath)  VALUES (1, 'http://localhost:8090/static/yum', 'localhost',  1, 0, 2, '/static/yum');
INSERT INTO cloudconductor.packageserver (id, description, path, servergroupid, indexertype, providertype, basepath)  VALUES (2, 'Ein TestRepo', 'http://irgendwo.com/yum',  2, 0, 2, '/static/yum');
INSERT INTO cloudconductor.packageserver (id, description, path, servergroupid, indexertype, providertype, basepath)  VALUES (3, 'Irgendwo Repo', 'http://irgendwoAnderst.com/yum',  2, 0, 2, '/static/yum');
INSERT INTO cloudconductor.packageserver (id, description, path, servergroupid, indexertype, providertype, basepath)  VALUES (4, 'Yanz weit weg', 'http://yanzweitweg.com/yum',  2, 0, 2, '/static/yum');
INSERT INTO cloudconductor.packageserver (id, description, path, servergroupid, indexertype, providertype, basepath)  VALUES (5, 'CloudConductor Repo', 'http://yum.cloudconductor.net/cloudconductor.repo',  3, 0, 2, 'http://yum.cloudconductor.net/cloudconductor.repo');

INSERT INTO cloudconductor.mappingpackageservertemplate VALUES (1, 1);

INSERT INTO cloudconductor.host VALUES (1, 'host1', NULL, 1, 1381489459832, NULL, false, false, false);

INSERT INTO cloudconductor.packageversion VALUES (1, 1, NULL, '9.2.4-1PGDG.rhel6');
INSERT INTO cloudconductor.packageversion VALUES (2, 2, NULL, '9.2.4-1PGDG.rhel6');
INSERT INTO cloudconductor.packageversion VALUES (3, 5, NULL, '9.2.4-1PGDG.rhel6');
INSERT INTO cloudconductor.packageversion VALUES (4, 3, NULL, '1.5.3-1');
INSERT INTO cloudconductor.packageversion VALUES (5, 4, NULL, '1.7.0_45-fcs');
INSERT INTO cloudconductor.packageversion VALUES (6, 6, NULL, '0.10.12-1');

INSERT INTO cloudconductor.mappingversiongroup VALUES (1,1)
INSERT INTO cloudconductor.mappingversiongroup VALUES (2,1)
INSERT INTO cloudconductor.mappingversiongroup VALUES (3,1)
INSERT INTO cloudconductor.mappingversiongroup VALUES (4,1)
INSERT INTO cloudconductor.mappingversiongroup VALUES (5,1)
INSERT INTO cloudconductor.mappingversiongroup VALUES (6,1) 


INSERT INTO cloudconductor.mappingrpmtemplate VALUES (1, 1, 1);
INSERT INTO cloudconductor.mappingrpmtemplate VALUES (2, 1, 2);
INSERT INTO cloudconductor.mappingrpmtemplate VALUES (3, 1, 3);
INSERT INTO cloudconductor.mappingrpmtemplate VALUES (4, 1, 4);
INSERT INTO cloudconductor.mappingrpmtemplate VALUES (5, 1, 5);

INSERT INTO cloudconductor.mappingsvcpkg VALUES (1, 5, 1);
INSERT INTO cloudconductor.mappingsvcpkg VALUES (2, 3, 2);

UPDATE cloudconductor.template SET smoothupdate=true WHERE id=1;

SET DATABASE REFERENTIAL INTEGRITY TRUE;