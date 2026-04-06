/** @type {import('next').NextConfig} */
const nextConfig = {
	output: "standalone",
	transpilePackages: ["@uikit/react", "uikit-common"],
};

export default nextConfig;
