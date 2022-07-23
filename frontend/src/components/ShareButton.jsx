import React, { useCallback, useState } from 'react';
import { Button, Tooltip } from '@mui/material';

export const ShareButton = ({ sharableText, ...rest }) => {
	const [open, setOpen] = useState(false);

	const share = useCallback(() => {
		navigator.clipboard.writeText(sharableText);

		setOpen(true);

		setTimeout(() => {
			setOpen(false);
		}, 2000);
	}, [sharableText]);

	if (open) {
		return (
			<Tooltip title="Copied!" open>
				<Button onClick={share} {...rest} />
			</Tooltip>
		);
	}

	// onClick is overwriteble by design
	return <Button onClick={share} {...rest} />;
};
