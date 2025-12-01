const fs = require('fs');
const path = require('path');

// ==========================================
// 1. HELPER FUNCTIONS
// ==========================================

const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

const loadTemplate = (filename) => {
  const templatePath = path.join(__dirname, '../blueprints/templates', filename);
  try {
    return fs.readFileSync(templatePath, 'utf8');
  } catch (e) {
    console.error(`Error loading template: ${templatePath}`);
    process.exit(1);
  }
};

const fillTemplate = (template, resourceName) => {
  return template
    .replace(/{{ResourceName}}/g, resourceName)
    .replace(/{{ResourceNameLower}}/g, resourceName.toLowerCase());
};

// ==========================================
// 2. CONTENT BUILDERS
// ==========================================

const buildInterfaceProps = (fields) => {
  return fields.map(f => {
    let type = 'string';
    if (f.type === 'auto_id') type = 'number';
    if (f.type === 'ref') type = 'any'; 
    return `  ${f.name}: ${type};`;
  }).join('\n');
};

const buildTableHeaders = (fields) => {
  return fields.map(f => `        <th>${capitalize(f.name)}</th>`).join('\n');
};

const buildTableRows = (fields) => {
  return fields.map(f => {
    if (f.type === 'ref') return `        <td>{{ item.${f.name}?.id || item.${f.name} }}</td>`; 
    return `        <td>{{ item.${f.name} }}</td>`;
  }).join('\n');
};

const buildRefLogic = (fields) => {
  return fields.map(f => {
    if (f.type === 'ref') {
      // If the field is a Reference, wrap the ID in an object
      return `      if (payload.${f.name}) {\n        payload.${f.name} = { id: Number(payload.${f.name}) };\n      }`;
    }
    return '';
  }).join('\n');
};

const buildFormControls = (fields) => {
  return fields.map(f => {
    if (f.name === 'id') return ''; 
    return `      ${f.name}: ['']`; 
  }).filter(Boolean).join(',\n');
};

const buildFormFields = (fields) => {
  return fields.map(f => {
    if (f.name === 'id') return ''; 

    let inputHtml = '';
    if (f.type === 'choice' && f.options) {
      const options = f.options.map(opt => `<option value="${opt}">${opt}</option>`).join('\n');
      inputHtml = `<select formControlName="${f.name}" id="${f.name}">
          <option value="">Select ${f.name}</option>
          ${options}
        </select>`;
    } else if (f.type === 'ref') {
      inputHtml = `<input type="number" formControlName="${f.name}" id="${f.name}" placeholder="Enter ${f.name} ID">`;
    } else {
      inputHtml = `<input type="text" formControlName="${f.name}" id="${f.name}">`;
    }

    return `<div class="form-group">
      <label for="${f.name}">${capitalize(f.name)}</label>
      ${inputHtml}
    </div>`;
  }).join('\n');
};

const getCreatePermissions = (resourceName, endpointsByRole) => {
  const allowed = [];
  if (!endpointsByRole) return "'ANY'"; 
  Object.keys(endpointsByRole).forEach(role => {
    const actions = endpointsByRole[role];
    const hasCreate = actions.some(a => a.resource === resourceName && a.action === 'create');
    if (hasCreate) allowed.push(`'ROLE_${role.toUpperCase()}'`);
  });
  return allowed.length === 0 ? "" : allowed.join(', ');
};

// ==========================================
// 3. MAIN EXECUTION
// ==========================================

const configFile = process.argv[2];
if (!configFile) {
  console.error("Config file argument missing.");
  process.exit(1);
}

const outputArg = process.argv[3];
const OUTPUT_DIR = outputArg 
  ? path.resolve(outputArg) 
  : path.join(__dirname, '../../output/frontend/src/app/features/generated');

console.log(`Output Directory: ${OUTPUT_DIR}`);

let config;
try {
  config = JSON.parse(fs.readFileSync(configFile, 'utf8'));
} catch (e) {
  console.error("Error reading config file.");
  process.exit(1);
}

if (fs.existsSync(OUTPUT_DIR)) {
  fs.rmSync(OUTPUT_DIR, { recursive: true, force: true });
}
fs.mkdirSync(OUTPUT_DIR, { recursive: true });

const generatedRoutes = [];

// --- A. Generate Standard Resources ---
if (config.resources) {
    config.resources.forEach(res => {
      const resourceName = res.name; 
      const folderName = resourceName.toLowerCase(); 
      const resourcePath = path.join(OUTPUT_DIR, folderName);
      
      fs.mkdirSync(resourcePath);

      // 1. Model
      let modelContent = loadTemplate('model.tpl');
      modelContent = fillTemplate(modelContent, resourceName);
      modelContent = modelContent.replace('{{InterfaceProps}}', buildInterfaceProps(res.fields));
      fs.writeFileSync(path.join(resourcePath, `${folderName}.model.ts`), modelContent);

      // 2. Service
      let serviceContent = loadTemplate('service.tpl');
      serviceContent = fillTemplate(serviceContent, resourceName);
      fs.writeFileSync(path.join(resourcePath, `${folderName}.service.ts`), serviceContent);

      // 3. List TS
      let listTsContent = loadTemplate('list.component.ts.tpl');
      listTsContent = fillTemplate(listTsContent, resourceName);
      const allowedRoles = getCreatePermissions(resourceName, config.endpoints_by_role);
      listTsContent = listTsContent.replace('{{AllowedRoles}}', allowedRoles);
      fs.writeFileSync(path.join(resourcePath, `${folderName}-list.component.ts`), listTsContent);

      // 4. List HTML
      let listHtmlContent = loadTemplate('list.component.html.tpl');
      listHtmlContent = fillTemplate(listHtmlContent, resourceName);
      listHtmlContent = listHtmlContent.replace('{{TableHeaders}}', buildTableHeaders(res.fields));
      listHtmlContent = listHtmlContent.replace('{{TableRows}}', buildTableRows(res.fields));
      fs.writeFileSync(path.join(resourcePath, `${folderName}-list.component.html`), listHtmlContent);

      // 5. Form TS
      let formTsContent = loadTemplate('form.component.ts.tpl');
      formTsContent = fillTemplate(formTsContent, resourceName);
      formTsContent = formTsContent.replace('{{FormControls}}', buildFormControls(res.fields));
      
      formTsContent = formTsContent.replace('{{RefLogic}}', buildRefLogic(res.fields));
      fs.writeFileSync(path.join(resourcePath, `${folderName}-form.component.ts`), formTsContent);

      // 6. Form HTML
      let formHtmlContent = loadTemplate('form.component.html.tpl');
      formHtmlContent = fillTemplate(formHtmlContent, resourceName);
      formHtmlContent = formHtmlContent.replace('{{FormFields}}', buildFormFields(res.fields));
      fs.writeFileSync(path.join(resourcePath, `${folderName}-form.component.html`), formHtmlContent);

      generatedRoutes.push({
        path: `${folderName}s`, 
        listComponent: `${resourceName}ListComponent`,
        listPath: `./${folderName}/${folderName}-list.component`,
        formPath: `${folderName}s/new`,
        formComponent: `${resourceName}FormComponent`,
        formImportPath: `./${folderName}/${folderName}-form.component`,
        isStatic: false
      });

      console.log(`Generated Module: ${resourceName}`);
    });
}

// --- B. Generate Static Profile Module ---
console.log('Generating User Profile Module...');
const profilePath = path.join(OUTPUT_DIR, 'profile');
fs.mkdirSync(profilePath);

const profileTsContent = loadTemplate('profile.component.ts.tpl');
fs.writeFileSync(path.join(profilePath, 'profile.component.ts'), profileTsContent);

const profileHtmlContent = loadTemplate('profile.component.html.tpl');
fs.writeFileSync(path.join(profilePath, 'profile.component.html'), profileHtmlContent);

generatedRoutes.push({
  path: 'profile',
  listComponent: 'ProfileComponent',
  listPath: './profile/profile.component',
  isStatic: true 
});

// --- C. Generate Routes File ---
const imports = generatedRoutes.map(r => {
  if (r.isStatic) {
    return `import { ${r.listComponent} } from '${r.listPath}';`;
  }
  return `import { ${r.listComponent} } from '${r.listPath}';
import { ${r.formComponent} } from '${r.formImportPath}';`;
}).join('\n');

const routesArr = generatedRoutes.map(r => {
  if (r.isStatic) {
    return `  { path: '${r.path}', component: ${r.listComponent} }`;
  }
  return `  { path: '${r.path}', component: ${r.listComponent} },
  { path: '${r.formPath}', component: ${r.formComponent} }`;
}).join(',\n');

const routesContent = `import { Routes } from '@angular/router';
${imports}

export const GENERATED_ROUTES: Routes = [
${routesArr}
];
`;

fs.writeFileSync(path.join(OUTPUT_DIR, 'routes.gen.ts'), routesContent);
console.log(`Routes generated at: ${path.join(OUTPUT_DIR, 'routes.gen.ts')}`);