# Contributing to ips-qr

First off, thank you for considering contributing to `nbs-ips-qr`! It is people like you who make this library a reliable tool for the community.

## Code of Conduct
By participating in this project, you are expected to uphold our [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting Started
- **Report bugs or suggest features:** Open an issue in this repository. Please provide a clear description and, if possible, steps to reproduce the bug.
- **Join the discussion:** If you are planning a large change, please open an issue first to discuss the approach.

## Prerequisites

See the [Build section in README.md](README.md#build) for required tooling (JDK, Maven).

## Development Workflow

### 1. Developer Certificate of Origin (DCO)
We use the **DCO** to manage contributions. You must certify that you have the right to submit your changes by signing off your commits:
```bash
git commit -s -m "Your commit message"
```
If you forget to sign off, you can amend your last commit:
```bash
git commit --amend -s
```

Our CI will block any PRs that do not include the sign-off.

### 2. Branching & Pull Requests

- Use a descriptive branch name (e.g., fix/qr-padding-issue or feat/go-implementation).
- Ensure all CI checks pass.
- Link your PR to the relevant issue.

### 3. Testing Standards

- **Java**: Run mvn test before submitting.

### 4. Dependency Hygiene
We aim for minimal dependencies.

- Do not add new dependencies without explicit team approval in a GitHub issue.
- All dependencies must be pinned to a specific version.

## Release Process
Maintainers handle the releases. When a feature or fix is ready:

1. We tag the release (e.g., v1.0.1).
2. The CI workflow builds, signs, and publishes the artifacts to Maven Central.
3. A GitHub Release is created automatically.

## License

By contributing, you agree that your contributions will be dual-licensed under the **[MIT License](LICENSE-MIT) OR [Apache License 2.0](LICENSE-APACHE)**, at the recipient's option — the same terms as the rest of the project.

Happy coding!