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

If the GitHub Private Vulnerability Reporting feature is unavailable, you may alternatively email us at **contact@dappulse.com**.

### Response Timeline
- **48 hours** — We will acknowledge receipt of your report.
- **7 days** — We will confirm the vulnerability and communicate our intended course of action.
- **90 days** — We aim to resolve the issue within 90 days. We ask that you refrain from public disclosure until a fix has been released or this period has elapsed, whichever comes first.

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

## Dependency Security Policy

### 21-Day Quarantine Rule

To guard against supply-chain attacks, this project enforces a **21-day quarantine** on all newly published dependency versions — across all semver levels (patch, minor, and major):

- A version is eligible for use only if it was published **at least 21 days ago**.
- This is enforced automatically: Dependabot is configured with `cooldown.default-days: 21` in [`.github/dependabot.yml`](.github/dependabot.yml), so update PRs are not opened until a version clears the quarantine window.
- **Security-update PRs bypass cooldown** — Dependabot always proposes a fix for a known vulnerability immediately, even if the patched version is newer than 21 days. Review those PRs on their own merits rather than delaying for the sake of the rule.

**Rationale**: Compromised packages are frequently caught within days or weeks of being published. A 21-day window gives the security community time to detect and report malicious releases before this project adopts them.

### Exception Process

If a version under the 21-day threshold must be adopted urgently (and it is not a security-update PR):

1. Manually bump the dependency in `pom.xml` outside of Dependabot, and open a PR that documents the urgency and the exact publish date.
2. **System architect** adds a written approval comment on the PR.
3. The PR may then be merged with the approval documented in the PR history.

### Auditing Current Dependencies

Check the publish date of a dependency on Maven Central:
```
https://central.sonatype.com/artifact/<groupId>/<artifactId>/<version>
```
The "Published" date is shown at the top of the version detail page.

To list available dependency/plugin updates locally:
```bash
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```