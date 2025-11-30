<div class="container">
  <div class="header-actions">
    <h2>{{ResourceName}} List</h2>
    
    <a *ngIf="canCreate" routerLink="/{{ResourceNameLower}}s/new" class="btn-primary">Create New</a>
  </div>
  
  <table class="data-table">
    <thead>
      <tr>
{{TableHeaders}}
      </tr>
    </thead>
    <tbody>
      <tr *ngFor="let item of items">
{{TableRows}}
      </tr>
    </tbody>
  </table>
</div>