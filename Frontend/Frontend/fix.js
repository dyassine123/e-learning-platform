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
        
        // Add imports
        if (!content.includes('import { HttpClient')) {
            content = "import { HttpClient, HttpParams } from '@angular/common/http';\n" + content;
        }
        if (!content.includes('import { Observable }')) {
            content = "import { Observable } from 'rxjs';\n" + content;
        }

        if (content !== originalContent) {
            fs.writeFileSync(filePath, content);
            console.log('Fixed ' + filePath);
        }
    }
});
