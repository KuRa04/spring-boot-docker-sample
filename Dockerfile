# ベースイメージとしてEclipse TemurinのJDK 17を使用
FROM eclipse-temurin:17

# 作業ディレクトリを指定
WORKDIR /var/www

# プロジェクト全体をコンテナにコピー
COPY . .

# 必要な依存関係を取得し、ビルド
RUN ./gradlew build --no-daemon

# アプリケーションを起動
CMD ["./gradlew", "bootRun"]
