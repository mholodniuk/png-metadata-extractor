import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChunkTableComponent } from './chunk-table.component';

describe('ChunkTableComponent', () => {
  let component: ChunkTableComponent;
  let fixture: ComponentFixture<ChunkTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChunkTableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChunkTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
