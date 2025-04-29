import { type JSX } from 'react';
import { useIntl } from 'react-intl';

import { type ResourceInterface } from 'src/store/slices/resources';

import { type HeaderKeys } from '../constants';
import { Link, Text } from './CellRenderer.styled';

function CreatedAtCell({ item }: { item: ResourceInterface }) {
	const { locale } = useIntl();

	return (
		<Text>
			{Intl.DateTimeFormat(locale, {
				year: 'numeric',
				month: 'numeric',
				day: 'numeric',
				hour: 'numeric',
				minute: 'numeric',
			}).format(new Date(item.createdAt))}
		</Text>
	);
}

const MAP_KEY_TO_CELL = {
	title: (item: ResourceInterface) => <Text shouldShowBold>{item.title}</Text>,
	type: (item: ResourceInterface) => <Text>{item.type}</Text>,
	link: (item: ResourceInterface) => <Link>{item.link}</Link>,
	description: (item: ResourceInterface) => <Text>{item.description}</Text>,
	tags: (item: ResourceInterface) => (
		<Text>{item.tags?.join(', ') ?? null}</Text>
	),
	createdAt: (item: ResourceInterface) => <CreatedAtCell item={item} />,
} as const satisfies Record<
	HeaderKeys,
	(item: ResourceInterface) => JSX.Element
>;

function cellContentRenderer({
	item,
	key,
}: {
	item: ResourceInterface;
	key: string;
}) {
	const renderer = key && MAP_KEY_TO_CELL[key as HeaderKeys];

	return renderer ? renderer(item) : null;
}

export default cellContentRenderer;
