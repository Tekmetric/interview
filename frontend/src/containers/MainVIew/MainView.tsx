import NavBar from 'src/components/NavBar';

import ResourcesTable from '../../components/ResourcesTable';
import { Container, ContentWrapper } from './MainView.styled';
import Toolbar from '../../components/Toolbar';

interface MainViewInterface extends React.ComponentProps<typeof NavBar> {}

function MainView({ onThemeSwitch, onLocaleSwitch }: MainViewInterface) {
	return (
		<Container>
			<NavBar onThemeSwitch={onThemeSwitch} onLocaleSwitch={onLocaleSwitch} />

			<ContentWrapper>
				<Toolbar />

				<ResourcesTable />
			</ContentWrapper>
		</Container>
	);
}

export default MainView;
