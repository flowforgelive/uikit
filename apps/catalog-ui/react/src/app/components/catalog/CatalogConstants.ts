import { CatalogOptions } from "catalog-shared";

export const DIR_OPTIONS = CatalogOptions.getInstance().dirOptions.map((o: any) => ({ id: o.id, label: o.label }));
export const SIZE_OPTIONS = CatalogOptions.getInstance().sizeOptions.map((o: any) => ({ id: o.id.toLowerCase(), label: o.label }));
export const RADIUS_OPTIONS = CatalogOptions.getInstance().radiusOptions.map((o: any) => ({ id: o.id, label: o.label }));
export const RADIUS_FRACTION_MAP: Record<string, number> = Object.fromEntries(
	CatalogOptions.getInstance().radiusOptions.map((o: any) => [o.id, CatalogOptions.getInstance().radiusFraction(o.id)]),
);
export const MAX_CONTAINER_RADIUS_MAP: Record<string, number> = Object.fromEntries(
	CatalogOptions.getInstance().radiusOptions.map((o: any) => [o.id, CatalogOptions.getInstance().maxContainerRadius(o.id)]),
);
export const PANEL_VARIANT_OPTIONS = CatalogOptions.getInstance().panelVariantOptions.map((o: any) => ({ id: o.id, label: o.label }));
export const PANEL_SIDE_OPTIONS = CatalogOptions.getInstance().panelSideOptions.map((o: any) => ({ id: o.id, label: o.label }));
