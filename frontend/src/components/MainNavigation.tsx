import { NavigationMenu, NavigationMenuList } from '@radix-ui/react-navigation-menu';
import { NavigationMenuItem, NavigationMenuLink } from './ui/navigation-menu';
import { Link } from '@tanstack/react-router';
import {
  ANIMAL_KINGDOM_ENTITY_ID,
  EPCOT_ENTITY_ID,
  HOLLYWOOD_STUDIOS_ENTITY_ID,
  MAGIC_KINGDOM_ENTITY_ID,
} from '@/constants';

export default function MainNavigation() {
  const routes = [
    { name: 'Home', to: '/' },
    { name: 'Magic Kingdom', to: `/parks/$parkId`, params: { parkId: MAGIC_KINGDOM_ENTITY_ID } },
    { name: 'EPCOT', to: `/parks/$parkId`, params: { parkId: EPCOT_ENTITY_ID } },
    { name: 'Hollywood Studios', to: `/parks/$parkId`, params: { parkId: HOLLYWOOD_STUDIOS_ENTITY_ID } },
    { name: 'Animal Kingdom', to: `/parks/$parkId`, params: { parkId: ANIMAL_KINGDOM_ENTITY_ID } },
  ];

  return (
    <NavigationMenu className="px-4 py-2 border-b border-slate-200">
      <NavigationMenuList className="max-w-2xl mx-auto flex gap-4 ">
        {routes.map((route, index) => (
          <NavigationMenuItem key={route.params?.parkId ?? route.to} className={`hidden lg:block ${index === 0 && 'block'}`}>
            <NavigationMenuLink asChild>
              <Link to={route.to} params={route.params} className="group">
                <span className="group-[&.active]:border-b border-slate-400">{route.name}</span>
              </Link>
            </NavigationMenuLink>
          </NavigationMenuItem>
        ))}
      </NavigationMenuList>
    </NavigationMenu>
  );
}
