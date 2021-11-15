import { TestBed } from '@angular/core/testing';

import { TouristicPointService } from './touristic-point.service';

describe('TouristicPointService', () => {
  let service: TouristicPointService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TouristicPointService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
