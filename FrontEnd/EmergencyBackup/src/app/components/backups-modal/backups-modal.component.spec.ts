import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackupsModalComponent } from './backups-modal.component';

describe('BackupsModalComponent', () => {
  let component: BackupsModalComponent;
  let fixture: ComponentFixture<BackupsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BackupsModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BackupsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
