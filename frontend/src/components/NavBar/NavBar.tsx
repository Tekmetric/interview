import { useIntl } from 'react-intl';
import { useTheme } from 'styled-components';

import { ButtonsContainer, Container, Title } from './NavBar.styled';
import Button from '../Button';
import DarkMode from '../Icons/DarkMode';
import LightMode from '../Icons/LightMode';

export interface NameBarInterface {
	onThemeSwitch: () => void;
	onLocaleSwitch: () => void;
}

function NavBar({ onThemeSwitch, onLocaleSwitch }: NameBarInterface) {
	const { formatMessage, locale } = useIntl();
	const theme = useTheme();

	return (
		<Container>
			<Title>{formatMessage({ id: 'LEARNING_RESOURCES' })}</Title>

			<ButtonsContainer>
				<Button onClick={onLocaleSwitch}>{locale}</Button>
				<Button isRound onClick={onThemeSwitch}>
					{theme.themeName === 'dark' ? (
						<LightMode fill={theme.textColor01} />
					) : (
						<DarkMode fill={theme.textColor01} />
					)}
				</Button>
			</ButtonsContainer>
		</Container>
	);
}

export default NavBar;
