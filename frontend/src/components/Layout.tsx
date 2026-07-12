import { useEffect, useRef } from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import { Link, NavLink, Outlet, useLocation } from 'react-router';
import styled from 'styled-components';

import { LocaleSwitcher } from '../features/locale/LocaleSwitcher';
import { RandomCharacterButton } from '../features/random/RandomCharacterButton';
import { ThemeToggle } from '../features/theme/ThemeToggle';

// Hidden until focused: keyboard users can jump straight past the header.
const SkipLink = styled.a`
  position: absolute;
  top: ${({ theme }) => theme.space.sm};
  left: ${({ theme }) => theme.space.sm};
  z-index: 10;
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.md}`};
  border-radius: ${({ theme }) => theme.radius.sm};
  background: ${({ theme }) => theme.colors.accent};
  color: ${({ theme }) => theme.colors.onAccent};
  font-weight: ${({ theme }) => theme.font.weight.medium};
  text-decoration: none;
  transform: translateY(-200%);

  &:focus-visible {
    transform: none;
  }
`;

const Header = styled.header`
  border-bottom: 1px solid ${({ theme }) => theme.colors.border};
  background: ${({ theme }) => theme.colors.surface};
`;

const HeaderInner = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: ${({ theme }) => theme.space.md};
  max-width: ${({ theme }) => theme.maxContentWidth};
  margin: 0 auto;
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.md}`};
`;

const Brand = styled(Link)`
  font-size: ${({ theme }) => theme.font.size.lg};
  font-weight: ${({ theme }) => theme.font.weight.bold};
  color: ${({ theme }) => theme.colors.accent};
  text-decoration: none;
`;

const Nav = styled.nav`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.space.xs};
  margin-inline-end: auto;
`;

// React Router sets aria-current="page" on the active NavLink; styling that
// attribute keeps the visual state and the accessibility state identical.
const NavItem = styled(NavLink)`
  padding: ${({ theme }) => `${theme.space.xs} ${theme.space.sm}`};
  border-radius: ${({ theme }) => theme.radius.sm};
  color: ${({ theme }) => theme.colors.textMuted};
  font-weight: ${({ theme }) => theme.font.weight.medium};
  text-decoration: none;

  &:hover {
    background: ${({ theme }) => theme.colors.surfaceHover};
    color: ${({ theme }) => theme.colors.text};
  }

  &[aria-current='page'] {
    background: ${({ theme }) => theme.colors.accentSoft};
    color: ${({ theme }) => theme.colors.text};
  }
`;

const Controls = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.space.sm};
`;

const Main = styled.main`
  max-width: ${({ theme }) => theme.maxContentWidth};
  margin: 0 auto;
  padding: ${({ theme }) => theme.space.lg} ${({ theme }) => theme.space.md};

  /* Focus target on navigation, but no visible ring on the page itself. */
  &:focus {
    outline: none;
  }
`;

// SPA route changes don't move focus by themselves, so screen reader and
// keyboard users would be stranded on the old page. Focusing <main> after
// each navigation restores the browser-like experience.
function useFocusMainOnNavigate(mainRef: React.RefObject<HTMLElement | null>) {
  const { pathname } = useLocation();
  const isFirstRender = useRef(true);

  useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }
    mainRef.current?.focus();
  }, [pathname, mainRef]);
}

export function Layout() {
  const intl = useIntl();
  const mainRef = useRef<HTMLElement>(null);
  useFocusMainOnNavigate(mainRef);

  return (
    <>
      <SkipLink href="#main">
        <FormattedMessage id="header.skipToContent" />
      </SkipLink>
      <Header>
        <HeaderInner>
          <Brand to="/">
            <FormattedMessage id="app.title" />
          </Brand>
          <Nav aria-label={intl.formatMessage({ id: 'nav.label' })}>
            <NavItem to="/characters">
              <FormattedMessage id="nav.characters" />
            </NavItem>
            <NavItem to="/episodes">
              <FormattedMessage id="nav.episodes" />
            </NavItem>
            <NavItem to="/locations">
              <FormattedMessage id="nav.locations" />
            </NavItem>
            <NavItem to="/favorites">
              <FormattedMessage id="nav.favorites" />
            </NavItem>
          </Nav>
          <Controls>
            <RandomCharacterButton />
            <ThemeToggle />
            <LocaleSwitcher />
          </Controls>
        </HeaderInner>
      </Header>
      <Main id="main" tabIndex={-1} ref={mainRef}>
        <Outlet />
      </Main>
    </>
  );
}
