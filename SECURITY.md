# Security Policy

## Reporting a Vulnerability

The `dapp-pulse` team takes the security of `nbs-ips-qr` seriously. We appreciate the efforts of security researchers and developers who help us improve the project by responsibly disclosing vulnerabilities.

If you believe you have found a security vulnerability in `nbs-ips-qr`, please **do not open a public GitHub issue**. Doing so may expose the vulnerability to malicious actors.

### Private Disclosure
Please report all security vulnerabilities via the **GitHub Private Vulnerability Reporting** feature:

1. Navigate to the [dapp-pulse/ips-qr repository](https://github.com/dapp-pulse/nbs-ips-qr).
2. Click the **Security** tab.
3. Click **Report a vulnerability**.
4. Fill in the details of the vulnerability.

We will acknowledge your report within 48 hours and work with you to verify and resolve the issue.

## Vulnerability Disclosure Process
1. **Report:** Researcher submits a report privately via GitHub.
2. **Review:** The maintainer team reviews the report and validates the impact.
3. **Fix:** We develop and test a fix.
4. **Coordination:** We coordinate a release date with you and publish a GitHub Security Advisory (GHSA), which automatically notifies users via their dependency graphs.
5. **Full Disclosure:** Once patched, we may publish a CVE/GHSA entry to explain the fix.

## Supported Versions
We prioritize security updates for the current major version. We generally do not backport security fixes to older versions unless they are critical and requested by a significant portion of our user base.

## Security Guarantees
- **No malicious dependencies:** All dependencies are pinned and verified.
- **Signed commits:** We follow a DCO-based workflow to ensure code provenance.
- **Automated Scanning:** We run daily automated security scans and CodeQL analysis.