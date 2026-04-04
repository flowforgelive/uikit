import React from "react";

const svgStyle: React.CSSProperties = {
	display: "block",
	fill: "none",
	stroke: "currentColor",
	strokeWidth: 2,
	strokeLinecap: "round",
	strokeLinejoin: "round",
};

const svgProps = {
	xmlns: "http://www.w3.org/2000/svg",
	viewBox: "0 0 24 24",
	width: "100%",
	height: "100%",
	style: svgStyle,
};

export const searchIcon = (
	<svg {...svgProps}>
		<circle cx="11" cy="11" r="8" />
		<path d="m21 21-4.35-4.35" />
	</svg>
);

export const starIcon = (
	<svg {...svgProps} style={{ ...svgStyle, fill: "currentColor", stroke: "none" }}>
		<path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
	</svg>
);

export const checkIcon = (
	<svg {...svgProps}>
		<path d="M20 6L9 17l-5-5" />
	</svg>
);

export const closeIcon = (
	<svg {...svgProps}>
		<path d="M18 6L6 18M6 6l12 12" />
	</svg>
);

export const plusIcon = (
	<svg {...svgProps}>
		<path d="M12 5v14M5 12h14" />
	</svg>
);

export const settingsIcon = (
	<svg {...svgProps}>
		<circle cx="12" cy="12" r="3" />
		<path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" />
	</svg>
);

export const arrowLeftIcon = (
	<svg {...svgProps}>
		<path d="M19 12H5M12 19l-7-7 7-7" />
	</svg>
);

export const arrowRightIcon = (
	<svg {...svgProps}>
		<path d="M5 12h14M12 5l7 7-7 7" />
	</svg>
);

export const chevronRightIcon = (
	<svg {...svgProps}>
		<path d="M9 18l6-6-6-6" />
	</svg>
);

export const ICON_BUTTON_SAMPLES = [searchIcon, plusIcon, starIcon, settingsIcon, closeIcon, checkIcon];
