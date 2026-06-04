const fs = require('fs');
const path = require('path');

function walkDir(dir, callback) {
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? walkDir(dirPath, callback) : callback(dirPath);
    });
}

walkDir('src/app/Services', (filePath) => {
    if (filePath.endsWith('.service.ts')) {
        let content = fs.readFileSync(filePath, 'utf8');
        let originalContent = content;
        
        // unify BASE_URL -> BaseUrl
        content = content.replace(/this\.BASE_URL/g, 'this.BaseUrl');
        content = content.replace(/readonly BASE_URL/g, 'readonly BaseUrl');
        
        // unify baseUrl -> BaseUrl
        content = content.replace(/this\.baseUrl/g, 'this.BaseUrl');
        content = content.replace(/readonly baseUrl/g, 'readonly BaseUrl');

        if (content !== originalContent) {
            fs.writeFileSync(filePath, content);
            console.log('Fixed BASE_URL casing in ' + filePath);
        }
    }
});
