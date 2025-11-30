<div class="form-container">
  <h2>Create {{ResourceName}}</h2>
  <form [formGroup]="form" (ngSubmit)="onSubmit()">
{{FormFields}}
    <button type="submit" [disabled]="!form.valid">Save</button>
  </form>
</div>