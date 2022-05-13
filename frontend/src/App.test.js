import React from 'react';
import renderer from 'react-test-renderer';
import App from './App';

const testRenderer = renderer.create(<App />);

describe('App', () => {
  let appJSON = testRenderer.toJSON();

  it('passes inital snapshot test', () => {
    expect(appJSON).toMatchSnapshot();
  });
});

afterAll(() => {
  testRenderer.unmount();
}, 0);