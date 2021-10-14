import { TestBed } from '@angular/core/testing';

import { VarService } from './var.service';

describe('VarService', () => {
  let service: VarService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
