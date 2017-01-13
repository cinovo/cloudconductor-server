import { UNGPage } from './app.po';

describe('ung App', function() {
  let page: UNGPage;

  beforeEach(() => {
    page = new UNGPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
