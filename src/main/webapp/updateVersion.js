const fs = require('fs');
const xml = fs.readFileSync('../../../pom.xml', 'utf8');
const parse = require('xml-parser');
const obj = parse(xml);
const fse = require('fs-extra')
const resolve = require('path');

const version = function find(obj) {
  for (let i = 0; i < obj.root.children.length; i++) {
    if (obj.root.children[i].name === "version") {
      return obj.root.children[i].content;
    }
  }
};


const file = resolve.resolve(__dirname, 'src', 'environments', 'version.ts');

fse.ensureFile(file).then(() => {
  fse.writeFileSync(file,
    `// IMPORTANT: THIS FILE IS AUTO GENERATED! DO NOT MANUALLY EDIT OR CHECKIN!
/* tslint:disable */
export const VERSION=${JSON.stringify(version(obj))};
/* tslint:enable */
`, {encoding: 'utf-8'});
  console.log("Successfully created version file");
}).catch(err => {
  console.log("Failed to create version file " + err);
});
