const fs = require('fs');
const path = require('path');

// --- Helper Functions ---
const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

const loadTemplate = (filename) => {
  return fs.readFileSync(path.join(__dirname, 'templates', filename), 'utf8');
};

const fillTemplate = (template, resourceName) => {
  return template
    .replace(/{{ResourceName}}/g, resourceName)
    .replace(/{{ResourceNameLower}}/g, resourceName.toLowerCase());
};

// --- Content Builders ---

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

// --- NEW: Permission Calculator ---
const getCreatePermissions = (resourceName, endpointsByRole) => {
  const allowed = [];
  if (!endpointsByRole) return "'ANY'"; // Fallback if no config

  Object.keys(endpointsByRole).forEach(role => {
    const actions = endpointsByRole[role];
    // Check if this role has 'create' action for this resource
    const hasCreate = actions.some(a => 
      a.resource === resourceName && a.action === 'create'
    );
    if (hasCreate) {
      // Map JSON roles to Backend Roles (usually prefixed with ROLE_)
      // Or keep them simple. Let's assume the token has "ADMIN" or "USER"
      allowed.push(`'${role}'`);
    }
  });

  if (allowed.length === 0) return ""; 
  return allowed.join(', ');
};

// --- Main Execution ---

const configFile = process.argv[2];
if (!configFile) {
  console.error("Please provide a path to the JSON config file.");
  process.exit(1);
}

const config = JSON.parse(fs.readFileSync(configFile, 'utf8'));
const OUTPUT_DIR = path.join(__dirname, '../src/app/features/generated');

if (fs.existsSync(OUTPUT_DIR)) {
  fs.rmSync(OUTPUT_DIR, { recursive: true, force: true });
}
fs.mkdirSync(OUTPUT_DIR, { recursive: true });

const generatedRoutes = [];

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

  // 3. List Component TS (With Permissions)
  let listTsContent = loadTemplate('list.component.ts.tpl');
  listTsContent = fillTemplate(listTsContent, resourceName);
  const allowedRoles = getCreatePermissions(resourceName, config.endpoints_by_role);
  listTsContent = listTsContent.replace('{{AllowedRoles}}', allowedRoles);
  fs.writeFileSync(path.join(resourcePath, `${folderName}-list.component.ts`), listTsContent);

  // 4. List Component HTML (With *ngIf)
  let listHtmlContent = loadTemplate('list.component.html.tpl');
  listHtmlContent = fillTemplate(listHtmlContent, resourceName);
  listHtmlContent = listHtmlContent.replace('{{TableHeaders}}', buildTableHeaders(res.fields));
  listHtmlContent = listHtmlContent.replace('{{TableRows}}', buildTableRows(res.fields));
  fs.writeFileSync(path.join(resourcePath, `${folderName}-list.component.html`), listHtmlContent);

  // 5. Form Component TS
  let formTsContent = loadTemplate('form.component.ts.tpl');
  formTsContent = fillTemplate(formTsContent, resourceName);
  formTsContent = formTsContent.replace('{{FormControls}}', buildFormControls(res.fields));
  fs.writeFileSync(path.join(resourcePath, `${folderName}-form.component.ts`), formTsContent);

  // 6. Form Component HTML
  let formHtmlContent = loadTemplate('form.component.html.tpl');
  formHtmlContent = fillTemplate(formHtmlContent, resourceName);
  formHtmlContent = formHtmlContent.replace('{{FormFields}}', buildFormFields(res.fields));
  fs.writeFileSync(path.join(resourcePath, `${folderName}-form.component.html`), formHtmlContent);

  // Register Route
  generatedRoutes.push({
    path: `${folderName}s`, 
    listComponent: `${resourceName}ListComponent`,
    listPath: `./${folderName}/${folderName}-list.component`,
    formPath: `${folderName}s/new`,
    formComponent: `${resourceName}FormComponent`,
    formImportPath: `./${folderName}/${folderName}-form.component`
  });

  console.log(`✅ Generated ${resourceName} module`);
});

// --- Generate Routes File ---

const imports = generatedRoutes.map(r => {
  return `import { ${r.listComponent} } from '${r.listPath}';
import { ${r.formComponent} } from '${r.formImportPath}';`;
}).join('\n');

const routesArr = generatedRoutes.map(r => {
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
console.log(`🚀 Routes generated at ${path.join(OUTPUT_DIR, 'routes.gen.ts')}`);