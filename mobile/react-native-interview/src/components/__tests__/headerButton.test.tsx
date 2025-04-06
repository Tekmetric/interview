import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import { Text } from 'react-native';
import HeaderButton from '../buttons/headerButton';

describe('HeaderButton', () => {
  it(`renders correctly`, () => {
    const tree = render(
      <HeaderButton onPress={() => console.log('test')}>
        <Text>snapshot</Text>
      </HeaderButton>,
    ).toJSON();

    expect(tree).toMatchSnapshot();
  });

  it('calls onPress when pressed', () => {
    const onPressMock = jest.fn();
    const { getByTestId } = render(
      <HeaderButton onPress={onPressMock}>
        <Text>press me</Text>
      </HeaderButton>,
    );

    fireEvent.press(getByTestId('header-button'));
    expect(onPressMock).toHaveBeenCalled();
  });
});
